package com.monyechi.aistorysculptor.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.monyechi.aistorysculptor.BuildConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.File
import javax.inject.Named

@HiltWorker
class DownloadBookWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Named("mainClient") private val okHttpClient: OkHttpClient
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val bookId = inputData.getString(KEY_BOOK_ID).orEmpty()
        val bookTitle = inputData.getString(KEY_BOOK_TITLE).orEmpty().ifBlank { "book_$bookId" }
        val format = inputData.getString(KEY_FORMAT).orEmpty().ifBlank { FORMAT_PDF }

        if (bookId.isBlank()) {
            return Result.failure(
                androidx.work.workDataOf(KEY_ERROR to "Missing book id")
            )
        }

        val template = if (format == FORMAT_DOCX) {
            BuildConfig.DOWNLOAD_DOCX_PATH_TEMPLATE
        } else {
            BuildConfig.DOWNLOAD_PDF_PATH_TEMPLATE
        }

        val relativePath = String.format(template, bookId)
        val resolvedUrl = BuildConfig.BASE_URL + relativePath
        val extension = if (format == FORMAT_DOCX) "docx" else "pdf"

        val request = Request.Builder()
            .url(resolvedUrl)
            .get()
            .build()

        return try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(
                        androidx.work.workDataOf(
                            KEY_ERROR to "Download failed: HTTP ${response.code}"
                        )
                    )
                }

                val responseBody = response.body
                    ?: return Result.failure(androidx.work.workDataOf(KEY_ERROR to "Empty response body"))

                val safeName = bookTitle
                    .trim()
                    .replace(" ", "_")
                    .replace("[^a-zA-Z0-9_\\-]".toRegex(), "")
                    .ifBlank { "book_$bookId" }

                val outputDirectory = File(applicationContext.getExternalFilesDir(null), "Download")
                if (!outputDirectory.exists()) {
                    outputDirectory.mkdirs()
                }

                val outputFile = File(outputDirectory, "${safeName}.$extension")
                outputFile.outputStream().use { outputStream ->
                    responseBody.byteStream().copyTo(outputStream)
                }

                Result.success(
                    androidx.work.workDataOf(KEY_FILE_PATH to outputFile.absolutePath)
                )
            }
        } catch (io: IOException) {
            Result.retry()
        } catch (t: Throwable) {
            Result.failure(
                androidx.work.workDataOf(KEY_ERROR to (t.message ?: "Unknown download error"))
            )
        }
    }

    companion object {
        const val KEY_BOOK_ID = "book_id"
        const val KEY_BOOK_TITLE = "book_title"
        const val KEY_FORMAT = "format"
        const val KEY_FILE_PATH = "file_path"
        const val KEY_ERROR = "error"

        const val FORMAT_PDF = "pdf"
        const val FORMAT_DOCX = "docx"
    }
}
