package com.monyechi.aistorysculptor.domain.model

/**
 * All dropdown/choice constants ported from Book_Assistant's models.py.
 * Single source of truth for the Android app.
 */
object BookConstants {

    // ── Book types ──────────────────────────────────────────────────────
    const val CHILDRENS_BOOK = "children"
    const val SELF_HELP = "self-help"
    const val FICTION_NOVEL = "fiction-novel"
    const val NON_FICTION_NOVEL = "non-fiction-novel"

    val BOOK_TYPE_CHOICES = listOf(
        CHILDRENS_BOOK to "Children's Book",
        SELF_HELP to "Self-Help",
        FICTION_NOVEL to "Fiction Novel",
        NON_FICTION_NOVEL to "Non-Fiction Novel",
    )

    // ── Novel genres ────────────────────────────────────────────────────
    val NOVEL_GENRE_CHOICES = listOf(
        "Art and Photography",
        "Autobiography",
        "Biography",
        "Children's Books",
        "Drama/Play",
        "Essays",
        "Fantasy",
        "Fiction",
        "Food and Cooking",
        "Graphic Novels/Comics",
        "Health and Wellness",
        "Historical Fiction",
        "History",
        "Memoir",
        "Middle-Grade (MG)",
        "Mystery",
        "Non-Fiction",
        "Philosophy",
        "Poetry",
        "Political Science",
        "Religion and Spirituality",
        "Romance",
        "Science",
        "Science Fiction",
        "Self-Help",
        "Sports and Recreation",
        "Thriller",
        "Travel",
        "True Crime",
        "Young Adult (YA)",
    )

    // ── Children's genres ───────────────────────────────────────────────
    val CHILD_GENRE_CHOICES = listOf(
        "Action and Excitement",
        "Adventure",
        "Adventure and Exploration",
        "Animals and Creatures",
        "Animals and Nature",
        "Bedtime Stories",
        "Comics",
        "Courage and Bravery",
        "Courage and Perseverance",
        "Creatures and Monsters",
        "Creativity and Imagination",
        "Curiosity and Learning",
        "Discovery and Wonder",
        "Empathy and Compassion",
        "Exploration",
        "Exploration and Adventure",
        "Fairy Tales",
        "Family and Relationships",
        "Family and Togetherness",
        "Fantasy",
        "Friendship",
        "Friendship and Cooperation",
        "Growth and Development",
        "Heroes and Heroines",
        "Heroes and Heroism",
        "Historical Fiction",
        "Humor",
        "Imagination and Creativity",
        "Inspiration and Dreams",
        "Jokes and Riddles",
        "Kindness and Empathy",
        "Language Learning",
        "Laughter and Fun",
        "Learning and Education",
        "Love and Kindness",
        "Magic and Wizards",
        "Mystery",
        "Nature and Environment",
        "Science Fiction",
        "Superheroes",
    )

    /** Return genres appropriate for the given book type. */
    fun genresForBookType(bookType: String): List<String> {
        return if (bookType == CHILDRENS_BOOK) CHILD_GENRE_CHOICES
        else NOVEL_GENRE_CHOICES + CHILD_GENRE_CHOICES
    }

    // ── POV ─────────────────────────────────────────────────────────────
    val POV_CHOICES = listOf(
        "First Person",
        "Second Person",
        "Third Person Omniscient",
        "Third Person Limited",
    )

    // ── Writing styles ──────────────────────────────────────────────────
    val WRITING_STYLE_CHOICES = listOf(
        "descriptive" to "Descriptive",
        "concise" to "Concise",
        "dialogue-driven" to "Dialogue-Driven",
        "stream-of-consciousness" to "Stream of Consciousness",
        "experimental" to "Experimental",
        "humorous" to "Humorous",
        "symbolic-allegorical" to "Symbolic/Allegorical",
        "realistic" to "Realistic",
        "highly-metaphorical" to "Highly Metaphorical",
        "informative" to "Informative",
    )

    // ── Languages ───────────────────────────────────────────────────────
    val LANGUAGE_CHOICES = listOf(
        "English",
        "Spanish",
        "French",
        "German",
        "Italian",
        "Portuguese",
        "Dutch",
        "Russian",
        "Chinese (Simplified & Traditional)",
        "Japanese",
        "Korean",
        "Arabic",
        "Hindi",
        "Greek",
        "Turkish",
        "Hebrew",
        "Thai",
        "Vietnamese",
        "Indonesian",
        "Malay",
        "Bengali",
        "Urdu",
        "Polish",
        "Czech",
        "Slovak",
        "Hungarian",
        "Romanian",
        "Bulgarian",
        "Ukrainian",
        "Serbian / Croatian / Bosnian",
    )

    // ── Chapter tones ───────────────────────────────────────────────────
    val TONE_CHOICES = listOf(
        "Ambiguous",
        "Cautious",
        "Challenging",
        "Compassionate",
        "Dark",
        "Detached",
        "Dramatic",
        "Dreamy",
        "Emotional",
        "Gloomy",
        "Hopeful",
        "Humorous",
        "Idealistic",
        "Indifferent",
        "Instructive",
        "Ironic",
        "Light",
        "Meaningless",
        "Mysterious",
        "Nostalgic",
        "Optimistic",
        "Playful",
        "Reflective",
        "Reverent",
        "Romantic",
        "Serious",
        "Simple",
        "Skeptical",
        "Suspenseful",
        "Symbolic",
        "Thoughtful",
        "True-to-Life",
        "Unique",
        "Vivid",
    )

    // ── Character roles ─────────────────────────────────────────────────
    val CHARACTER_ROLE_CHOICES = listOf(
        "Main Character",
        "Opposing Character",
        "Supporting",
    )

    // ── Word count ranges ───────────────────────────────────────────────
    /** Novel chapters: 1000–10000 in steps of 500 */
    val NOVEL_WORD_COUNT_CHOICES = (1000..10000 step 500).toList()

    /** Children's book chapters: 300–650 in steps of 50 */
    val CHILDRENS_WORD_COUNT_CHOICES = (300..650 step 50).toList()

    fun wordCountChoices(bookType: String): List<Int> {
        return if (bookType == CHILDRENS_BOOK) CHILDRENS_WORD_COUNT_CHOICES
        else NOVEL_WORD_COUNT_CHOICES
    }

    // ── Token economy ───────────────────────────────────────────────────
    const val TOKEN_COST_GENERATE_CHARACTER = 2
    const val TOKEN_COST_AUTO_GENERATE_CHAPTERS = 4
    const val TOKEN_COST_GENERATE_SUMMARY = 2
    const val TOKEN_COST_COVER_ART = 15

    /** Calculate token cost to render a chapter. */
    fun chapterRenderTokenCost(bookType: String, desiredWordCount: Int): Int {
        val divisor = if (bookType == CHILDRENS_BOOK) 25 else 200
        val cost = desiredWordCount / divisor
        return if (cost == 0 && desiredWordCount > 0) 1 else cost
    }

    // ── OpenAI defaults ─────────────────────────────────────────────────
    const val DEFAULT_TEMPERATURE = 1.3
    const val DEFAULT_TOP_P = 0.2
    const val CONVERSATION_TOKEN_BUDGET = 128_000
}
