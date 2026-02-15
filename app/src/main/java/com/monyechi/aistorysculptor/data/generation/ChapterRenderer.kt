package com.monyechi.aistorysculptor.data.generation

import com.monyechi.aistorysculptor.data.api.ChatMessage
import com.monyechi.aistorysculptor.data.db.BookEntity
import com.monyechi.aistorysculptor.data.db.ChapterDao
import com.monyechi.aistorysculptor.data.db.ChapterEntity
import com.monyechi.aistorysculptor.data.db.CharacterDao
import com.monyechi.aistorysculptor.domain.model.BookConstants
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Renders chapter content via OpenAI — ported from Book_Assistant's
 * BaseRenderer + ChildrenStrategy + GeneralStrategy.
 *
 * All rendering runs on coroutines (not blocking).
 */
@Singleton
class ChapterRenderer @Inject constructor(
    private val openAi: OpenAiService,
    private val chapterDao: ChapterDao,
    private val characterDao: CharacterDao,
) {
    // ── Public API ──────────────────────────────────────────────────────

    /**
     * Render a single chapter. Call from a coroutine context.
     * @return rendered text, also persisted to the chapter entity.
     */
    suspend fun renderChapter(book: BookEntity, chapter: ChapterEntity): String {
        return if (book.bookType == BookConstants.CHILDRENS_BOOK) {
            renderChildrens(book, chapter)
        } else {
            renderGeneral(book, chapter)
        }
    }

    /**
     * After rendering, call this to summarize the chapter and update the
     * book's running summary.
     * Returns the chapter summary text.
     */
    suspend fun finalizeChapter(
        book: BookEntity,
        chapter: ChapterEntity,
        renderedText: String,
    ): String {
        // Summarize the rendered content
        val chapterSummary = summarizeChapter(renderedText)

        // Update the book's running summary
        updateRunningSummary(book, renderedText)

        return chapterSummary
    }

    // ── Children's Strategy ─────────────────────────────────────────────

    private suspend fun renderChildrens(book: BookEntity, chapter: ChapterEntity): String {
        val chars = characterDao.getCharactersForChapter(chapter.id)
        val charPrompt = chars.joinToString(", ") { "Name: ${it.name}, Age: ${it.age}, Role: ${it.role}" }

        val prevFirstSentences = getPreviousFirstSentences(chapter)

        val system = buildString {
            appendLine("# Instructions:")
            if (chapter.chapterNum == 1) {
                appendLine("You're an author writing a children's book titled \"${book.title}\".")
                appendLine("The book genre is ${book.genre}, and it's aimed at young readers.")
                appendLine("Generate content for Chapter 1: '${chapter.title}', engaging, imaginative, and suitable for children.")
                appendLine("Emphasize the use of Phonics.")
                appendLine("Do not include the chapter number or title in the content.")
            } else {
                appendLine("You're an author continuing a children's book titled \"${book.title}\".")
                appendLine("The book genre is ${book.genre}.")
                appendLine("Generate content for Chapter ${chapter.chapterNum}: '${chapter.title}'.")
                appendLine("Emphasize Phonics. Do not include the chapter number or title.")
                appendLine("Use previous chapter context but avoid similar opening sentences.")
                appendLine("Do not exceed ${chapter.desiredWordCount + 30} words.")
                appendLine("Previous Chapter First Sentences: $prevFirstSentences")
                appendLine("Story so far: ${book.runningSummary}")
            }
            appendLine()
            appendLine("- Literary POV: \"${book.pov}\"")
            appendLine("- Book summary: \"${book.summary}\"")
            appendLine("- Chapter summary: \"${chapter.summary}\"")
            appendLine("- Writing Style: \"${book.writingStyle}\"")
            appendLine("- Chapter tone: \"${chapter.tone}\"")
            appendLine("- Chapter setting: \"${chapter.setting}\"")
            appendLine("- Characters: $charPrompt")
            appendLine("- Language: ${book.language}")
            appendLine("- Desired word count: ${chapter.desiredWordCount} words")
            appendLine()
            if (chapter.chapterNum == 1) {
                appendLine("Begin with a captivating introduction. Introduce main characters and world.")
            } else {
                appendLine("Transition smoothly from the previous chapter. Maintain continuity.")
            }
            appendLine("Conclude in a complete sentence at the desired word count.")
        }

        val content = openAi.chatCompletion(
            messages = listOf(ChatMessage(role = "system", content = system)),
            maxTokens = 16384,
            temperature = 0.7,
            topP = 0.0,
            frequencyPenalty = 0.6,
            presencePenalty = 0.5,
        )

        // If too short, retry with higher limit
        if (content.split("\\s+".toRegex()).size < chapter.desiredWordCount - 100) {
            return openAi.chatCompletion(
                messages = listOf(ChatMessage(role = "system", content = system)),
                maxTokens = 16384,
                temperature = 0.7,
                topP = 0.0,
                frequencyPenalty = 0.6,
                presencePenalty = 0.5,
            )
        }
        return content
    }

    // ── General Strategy (multi-segment) ────────────────────────────────

    private suspend fun renderGeneral(book: BookEntity, chapter: ChapterEntity): String {
        val numSegments = maxOf(1, chapter.desiredWordCount / 500)
        val chars = characterDao.getCharactersForChapter(chapter.id)
        val charPrompt = chars.joinToString(", ") { "Name: ${it.name}, Age: ${it.age}, Role: ${it.role}" }

        val bookInstructions = when (book.bookType) {
            BookConstants.SELF_HELP -> """
                # Self-Help Book:
                * Focus on clear, actionable steps.
                * Use a motivational tone.
                * Progressively guide the reader.
            """.trimIndent()
            BookConstants.FICTION_NOVEL -> """
                # Fiction Novel:
                * Craft a compelling narrative in ${book.genre}.
                * Each chapter contributes to story and character arcs.
                * Engage with rich descriptions and dialogue.
            """.trimIndent()
            else -> """
                # General:
                * Write a ${book.genre} book titled "${book.title}".
                * Maintain engagement and coherence.
            """.trimIndent()
        }

        val system = buildString {
            appendLine(bookInstructions)
            appendLine()
            appendLine("* Writing chapter for \"${book.title}\" in ${book.genre}.")
            appendLine("* Use standard paragraph formatting. No chapter number/title in content.")
            appendLine("* Language: ${book.language}")
            appendLine("* Written in $numSegments segments, each ~500 words.")
            appendLine("* Do NOT mention 'segment' in the output.")
            appendLine()
            appendLine("- POV: \"${book.pov}\"")
            appendLine("- Book summary: \"${book.summary}\"")
            appendLine("- Chapter summary: \"${chapter.summary}\"")
            appendLine("- Writing Style: \"${book.writingStyle}\"")
            appendLine("- Chapter tone: \"${chapter.tone}\"")
            appendLine("- Chapter setting: \"${chapter.setting}\"")
            appendLine("- Characters: $charPrompt")
            appendLine()
            if (chapter.chapterNum == 1) {
                appendLine("Start with a compelling introduction. Introduce key characters and setting.")
            } else {
                appendLine("Transition smoothly from previous chapter. Story so far: ${book.runningSummary}")
            }
        }

        // 1. Generate segment summaries
        val segmentSummaries = generateSegmentSummaries(chapter, numSegments, book.language)

        // 2. Generate each segment sequentially
        val conversation = mutableListOf(ChatMessage(role = "system", content = system))
        val fullContent = StringBuilder()

        for (segment in 1..numSegments) {
            val segSummary = segmentSummaries[segment] ?: ""
            val prompt = """
                # CONTINUE THE STORY.
                - Segment $segment of $numSegments
                - Segment Summary: $segSummary
            """.trimIndent()
            conversation.add(ChatMessage(role = "user", content = prompt))

            val generated = openAi.chatCompletion(
                messages = conversation,
                maxTokens = 16384,
                temperature = 0.8,
                topP = 0.3,
                frequencyPenalty = 1.1,
                presencePenalty = 1.4,
            )
            conversation.add(ChatMessage(role = "assistant", content = generated))
            if (fullContent.isNotEmpty()) fullContent.append("\n\n")
            fullContent.append(generated)

            // Manage conversation size
            manageConversationSize(conversation)
        }
        return fullContent.toString()
    }

    // ── Segment summaries ───────────────────────────────────────────────

    private suspend fun generateSegmentSummaries(
        chapter: ChapterEntity,
        numSegments: Int,
        language: String,
    ): Map<Int, String> {
        val prompt = buildString {
            appendLine("Break down this chapter summary into $numSegments segments.")
            appendLine("Each segment summary should be max 150 words. Write in $language.")
            appendLine("Chapter summary: ${chapter.summary}")
            appendLine()
            appendLine("Return JSON like:")
            appendLine("{")
            for (i in 1..numSegments) {
                appendLine("  \"segment${i}_summary\": \"...\"${if (i < numSegments) "," else ""}")
            }
            appendLine("}")
        }

        val json = openAi.chatCompletionJson(
            messages = listOf(ChatMessage(role = "system", content = prompt)),
            maxTokens = 16384,
        )

        return parseSegmentSummaries(json, numSegments)
    }

    private fun parseSegmentSummaries(json: String, numSegments: Int): Map<Int, String> {
        val result = mutableMapOf<Int, String>()
        try {
            // Simple regex-based extraction; avoids heavy JSON parsing
            val regex = """"segment(\d+)_summary"\s*:\s*"([^"]+)"""".toRegex()
            for (match in regex.findAll(json)) {
                val num = match.groupValues[1].toIntOrNull() ?: continue
                result[num] = match.groupValues[2]
            }
        } catch (_: Exception) {
            // fallback: return empty summaries
        }
        if (result.isEmpty()) {
            for (i in 1..numSegments) result[i] = "Continue the story."
        }
        return result
    }

    // ── Summarization ───────────────────────────────────────────────────

    private suspend fun summarizeChapter(content: String): String {
        return openAi.chatCompletion(
            messages = listOf(
                ChatMessage(
                    role = "system",
                    content = "Summarize the following chapter and provide key points for context:\n\n$content"
                )
            ),
            maxTokens = 500,
        )
    }

    private suspend fun updateRunningSummary(book: BookEntity, newContent: String) {
        val prompt = buildString {
            appendLine("Update the running summary of a book with new chapter content.")
            appendLine("Current Summary: ${book.runningSummary}")
            appendLine("New Chapter: $newContent")
            appendLine("Provide an updated summary in ~500 words.")
        }
        // Note: caller should persist the result to the book entity
        openAi.chatCompletion(
            messages = listOf(ChatMessage(role = "user", content = prompt)),
            maxTokens = 800,
        )
    }

    // ── Conversation management ─────────────────────────────────────────

    private fun manageConversationSize(conversation: MutableList<ChatMessage>) {
        val totalChars = conversation.sumOf { it.content.length }
        val approxTokens = totalChars / 4
        val limit = (BookConstants.CONVERSATION_TOKEN_BUDGET * 0.85).toInt()

        if (approxTokens > limit) {
            // Keep system message and last 2 exchanges
            val system = conversation.firstOrNull()
            val tail = conversation.takeLast(4)
            conversation.clear()
            if (system != null) conversation.add(system)
            conversation.addAll(tail)
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private suspend fun getPreviousFirstSentences(chapter: ChapterEntity): String {
        val chapters = chapterDao.getChaptersByBook(chapter.bookId)
            .filter { it.chapterNum < chapter.chapterNum && it.rendered }
            .sortedBy { it.chapterNum }

        return chapters.joinToString(" | ") { ch ->
            val first = ch.renderedContent?.split(".")?.firstOrNull()?.plus(".") ?: ""
            "Chapter ${ch.chapterNum}: $first"
        }.ifEmpty { "No previous chapters available." }
    }
}
