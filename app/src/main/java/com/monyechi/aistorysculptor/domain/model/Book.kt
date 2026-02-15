package com.monyechi.aistorysculptor.domain.model

/** Domain representation of a Book (mirrors Room BookEntity). */
data class Book(
    val id: Long,
    val userId: Long,
    val title: String,
    val author: String = "",
    val bookType: String = BookConstants.CHILDRENS_BOOK,
    val genre: String = "",
    val language: String = "English",
    val pov: String = "",
    val writingStyle: String = "",
    val summary: String = "",
    val runningSummary: String = "",
    val coverArtPath: String? = null,
    val isRenderingChapters: Boolean = false,
    val isGeneratingCoverArt: Boolean = false,
    val tokenChargeForImage: Int = 15,
    val createdAtIso: String = "",
) {
    val isFullyRendered: Boolean
        get() = false // will be computed from chapters externally
}

