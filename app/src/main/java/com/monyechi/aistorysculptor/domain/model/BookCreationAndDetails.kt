package com.monyechi.aistorysculptor.domain.model

data class CreateBookRequest(
    val genre: String,
    val ageGroup: String,
    val characterName: String,
    val characterDescription: String,
    val storyOutline: String
)

data class GenerationStatus(
    val jobId: String,
    val status: String,
    val bookId: String? = null,
    val progress: Int? = null,
    val message: String? = null
) {
    fun isComplete(): Boolean {
        val normalized = status.lowercase()
        return normalized == "completed" || normalized == "ready" || normalized == "done"
    }
}

data class BookChapter(
    val index: Int,
    val title: String,
    val content: String
)

data class BookDetails(
    val id: String,
    val title: String,
    val coverImageUrl: String?,
    val createdAtIso: String,
    val status: String,
    val chapters: List<BookChapter>,
    val downloadUrl: String?,
    val shareUrl: String?
)
