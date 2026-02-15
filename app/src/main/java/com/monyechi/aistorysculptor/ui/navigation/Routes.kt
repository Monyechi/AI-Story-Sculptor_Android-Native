package com.monyechi.aistorysculptor.ui.navigation

sealed class Route(val value: String) {
    data object AuthRoot : Route("auth")
    data object MainRoot : Route("main")

    data object Login : Route("auth/login")
    data object Register : Route("auth/register")

    data object Library : Route("main/library")
    data object CreateBook : Route("main/create")
    data object BookDetails : Route("main/details/{bookId}") {
        fun withId(bookId: Long): String = "main/details/$bookId"
    }
}
