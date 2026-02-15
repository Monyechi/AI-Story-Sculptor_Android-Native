package com.monyechi.aistorysculptor.data.generation

import com.monyechi.aistorysculptor.data.api.ChatMessage
import com.monyechi.aistorysculptor.data.db.BookEntity
import com.monyechi.aistorysculptor.data.db.ChapterDao
import com.monyechi.aistorysculptor.data.db.ChapterEntity
import com.monyechi.aistorysculptor.data.db.CharacterDao
import com.monyechi.aistorysculptor.data.db.CharacterEntity
import com.monyechi.aistorysculptor.domain.model.BookConstants
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles AI auto-generation of books summaries, chapters metadata, and characters.
 * Ported from Book_Assistant's model methods.
 */
@Singleton
class ContentGenerator @Inject constructor(
    private val openAi: OpenAiService,
    private val chapterDao: ChapterDao,
    private val characterDao: CharacterDao,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    // ── Book Summary ────────────────────────────────────────────────────

    /** Generate a compelling ~250-word summary for a book. */
    suspend fun generateBookSummary(book: BookEntity): String {
        val prompt = """
            In 250 words or less, write a compelling summary using these book details:
            - Title: ${book.title}
            - Genre: ${book.genre.ifBlank { "N/A" }}
            - Book Type: ${book.bookType}
            - Writing Style: ${book.writingStyle.ifBlank { "N/A" }}
            - Point of View: ${book.pov.ifBlank { "N/A" }}
        """.trimIndent()

        return openAi.chatCompletion(
            messages = listOf(
                ChatMessage("system", "You write concise, marketable book summaries."),
                ChatMessage("user", prompt),
            ),
            maxTokens = 300,
        )
    }

    // ── Chapter Summary ─────────────────────────────────────────────────

    /** Generate a chapter summary that continues from the previous chapter. */
    suspend fun generateChapterSummary(book: BookEntity, chapter: ChapterEntity): String {
        val prev = chapterDao.getPreviousChapter(chapter.bookId, chapter.chapterNum)
        val prevSummary = prev?.renderedSummary ?: prev?.summary ?: "N/A"
        val chars = characterDao.getCharactersForChapter(chapter.id)
        val charNames = chars.joinToString(", ") { it.name }.ifEmpty { "None" }

        val prompt = """
            Write a concise summary for THIS chapter (<= 500 characters) continuing from the last chapter.
            Write in ${book.language}.
            
            # BOOK
            - Title: ${book.title}
            - Summary: ${book.summary.ifBlank { "N/A" }}
            - Running Summary: ${book.runningSummary.ifBlank { "N/A" }}
            
            # PREVIOUS CHAPTER
            - Ended With: $prevSummary
            
            # THIS CHAPTER
            - Title: ${chapter.title}
            - Tone: ${chapter.tone ?: "N/A"}
            - Setting: ${chapter.setting ?: "N/A"}
            - Characters: $charNames
        """.trimIndent()

        return openAi.chatCompletion(
            messages = listOf(
                ChatMessage("system", "You generate concise chapter continuation summaries (<=500 characters)."),
                ChatMessage("user", prompt),
            ),
            maxTokens = 200,
        )
    }

    // ── Chapter Metadata Auto-Generation ────────────────────────────────

    /** Auto-generate chapter metadata (title, tone, setting, summary, characters). Returns a map. */
    suspend fun generateChapterDetails(
        book: BookEntity,
        existingChapterTitles: String,
        characterNames: String,
        lastChapterSummary: String?,
    ): Map<String, Any>? {
        val prompt = """
            # INSTRUCTIONS:
            1. You are writing the NEXT chapter for "${book.title}".
            2. Continue directly from the previous chapter; maintain continuity.
            3. Provide chapter details in JSON.
            4. Write in ${book.language}.
            
            % STORY SO FAR
            - Summary: ${book.summary.ifBlank { "N/A" }}
            - Running Summary: ${book.runningSummary.ifBlank { "N/A" }}
            - Previous Chapter Ended With: ${lastChapterSummary ?: "N/A"}
            
            % INPUT
            - Genre: ${book.genre}
            - Book Type: ${book.bookType}
            - Existing chapter titles: ${existingChapterTitles.ifBlank { "None" }}
            - Existing characters: ${characterNames.ifBlank { "None" }}
            
            % OUTPUT (JSON):
            {
                "title": "Chapter Title (max 40 chars)",
                "tone": "One of: ${BookConstants.TONE_CHOICES.joinToString(", ")}",
                "setting": "Setting (max 69 chars) or null for self-help",
                "summary": "Summary (max 300 chars)",
                "characters_involved": ["Character 1", "Character 2"]
            }
        """.trimIndent()

        val response = openAi.chatCompletionJson(
            messages = listOf(
                ChatMessage("system", "Follow the JSON schema exactly."),
                ChatMessage("user", prompt),
            ),
            maxTokens = 500,
        )

        return try {
            val obj = json.parseToJsonElement(response).jsonObject
            mapOf(
                "title" to (obj["title"]?.jsonPrimitive?.content ?: "Untitled"),
                "tone" to (obj["tone"]?.jsonPrimitive?.content ?: "Light"),
                "setting" to (obj["setting"]?.jsonPrimitive?.content ?: ""),
                "summary" to (obj["summary"]?.jsonPrimitive?.content ?: ""),
                "characters_involved" to (obj["characters_involved"]?.toString() ?: "[]"),
            )
        } catch (e: Exception) {
            null
        }
    }

    // ── Character Auto-Generation ───────────────────────────────────────

    /** Auto-generate a character for the given book. */
    suspend fun generateCharacter(book: BookEntity): CharacterEntity? {
        val existingChars = characterDao.getCharactersByBook(book.id)
        val existingNames = existingChars.map { it.name }
        val existingJson = existingNames.joinToString("\n").ifEmpty { "No Existing Characters" }
        val ageRange = if (book.bookType == BookConstants.CHILDRENS_BOOK) "1 to 12" else "1 to 105"

        val prompt = """
            % INSTRUCTIONS:
            - Create a character for a ${book.genre.ifBlank { "children's" }} book titled '${book.title}'.
            - Name, age ($ageRange), short bio, and role.
            - Do NOT duplicate existing characters.
            
            % BOOK DETAILS:
            - Title: ${book.title}
            - Genre: ${book.genre}
            - Type: ${book.bookType}
            - Summary: ${book.summary}
            - Existing characters: $existingJson
            
            % OUTPUT (JSON):
            {
                "first_name": "First Name",
                "last_name": "Last Name",
                "age": 25,
                "bio": "Brief bio",
                "role": "Main Character"
            }
        """.trimIndent()

        // Try up to 3 times for a unique name
        repeat(3) { attempt ->
            val response = openAi.chatCompletionJson(
                messages = listOf(
                    ChatMessage("system", "Follow the JSON schema exactly."),
                    ChatMessage("user", prompt),
                ),
                maxTokens = 350,
            )

            try {
                val obj = json.parseToJsonElement(response).jsonObject
                val firstName = obj["first_name"]?.jsonPrimitive?.content ?: return null
                val lastName = obj["last_name"]?.jsonPrimitive?.content ?: ""
                val fullName = "$firstName $lastName".trim()

                if (fullName in existingNames) return@repeat  // retry

                val age = obj["age"]?.jsonPrimitive?.content?.toIntOrNull()
                val bio = obj["bio"]?.jsonPrimitive?.content
                val role = obj["role"]?.jsonPrimitive?.content ?: "Supporting"
                val validRole = if (role in BookConstants.CHARACTER_ROLE_CHOICES) role else "Supporting"

                return CharacterEntity(
                    bookId = book.id,
                    name = fullName,
                    age = age,
                    bio = bio,
                    role = validRole,
                )
            } catch (_: Exception) {
                // retry
            }
        }
        return null
    }

    // ── Cover Art ───────────────────────────────────────────────────────

    /**
     * Generate a cover art image for the book.
     * Returns base64-encoded image data, or null on failure.
     */
    suspend fun generateCoverArt(
        book: BookEntity,
        userDescription: String? = null,
    ): String? {
        val extra = when (book.bookType) {
            BookConstants.CHILDRENS_BOOK -> "Embrace a playful, colorful, hand-drawn style that captivates children."
            BookConstants.SELF_HELP -> "Use a clean, elegant style aligned with self-help aesthetics."
            else -> "Align the artwork closely to the summary and genre."
        }

        val instructions = buildString {
            appendLine("Craft a detailed prompt for an image model to create a book cover illustration.")
            appendLine("- Prominently position the title '${book.title}' at the top.")
            appendLine("- No text other than the title. No hands, pencils, or 'drawing process' objects.")
            appendLine("- Keep the prompt 70–100 words. Leave ~64px margins.")
            appendLine("- $extra")
            if (!userDescription.isNullOrBlank()) {
                appendLine("- User description: $userDescription")
            }
            appendLine()
            appendLine("Book Details:")
            appendLine("- Title: ${book.title}")
            appendLine("- Genre: ${book.genre}")
            appendLine("- Summary: ${book.summary}")
        }

        // Step 1: Generate the image prompt via GPT
        val imagePrompt = openAi.chatCompletion(
            messages = listOf(ChatMessage("system", instructions)),
            maxTokens = 300,
            temperature = 0.4,
            topP = 0.4,
        )

        if (imagePrompt.isBlank()) return null

        // Step 2: Generate the actual image
        return openAi.generateImage(prompt = imagePrompt)
    }
}
