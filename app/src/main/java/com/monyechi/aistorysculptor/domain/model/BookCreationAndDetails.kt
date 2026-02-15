package com.monyechi.aistorysculptor.domain.model

/** Request data to create a new book (all metadata collected from the wizard). */
data class CreateBookRequest(
    val title: String,
    val author: String,
    val bookType: String,
    val genre: String,
    val language: String,
    val pov: String,
    val writingStyle: String,
    val summary: String,
)

/** Domain representation of a Chapter. */
data class Chapter(
    val id: Long = 0,
    val bookId: Long,
    val chapterNum: Int,
    val title: String,
    val setting: String? = null,
    val summary: String = "",
    val tone: String? = null,
    val desiredWordCount: Int = 1000,
    val rendered: Boolean = false,
    val renderedContent: String? = null,
    val renderedSummary: String? = null,
    val partialRenderedText: String? = null,
    val currentSegment: Int = 0,
) {
    val wordCount: Int
        get() = renderedContent?.split("\\s+".toRegex())?.size ?: 0

    /** Token charge for rendering this chapter. */
    fun tokenCharge(bookType: String): Int =
        BookConstants.chapterRenderTokenCost(bookType, desiredWordCount)
}

/** Domain representation of a Character. */
data class Character(
    val id: Long = 0,
    val bookId: Long,
    val name: String,
    val age: Int? = null,
    val bio: String? = null,
    val role: String,   // "Main Character" | "Opposing Character" | "Supporting"
)

/** Detailed view of a book: includes chapters + characters. */
data class BookDetails(
    val book: Book,
    val chapters: List<Chapter>,
    val characters: List<Character>,
)

/**
 * Rendering progress — emitted during chapter generation.
 * [currentChapter] / [totalChapters], plus optional word count.
 */
data class RenderProgress(
    val currentChapter: Int,
    val totalChapters: Int,
    val currentWords: Int = 0,
    val message: String = "",
)

