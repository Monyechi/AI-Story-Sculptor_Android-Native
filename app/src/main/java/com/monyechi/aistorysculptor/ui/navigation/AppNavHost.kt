package com.monyechi.aistorysculptor.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.monyechi.aistorysculptor.ui.screen.auth.LoginScreen
import com.monyechi.aistorysculptor.ui.screen.auth.RegisterScreen
import com.monyechi.aistorysculptor.ui.screen.create.CreateBookScreen
import com.monyechi.aistorysculptor.ui.screen.details.BookDetailsScreen
import com.monyechi.aistorysculptor.ui.screen.library.LibraryScreen
import com.monyechi.aistorysculptor.ui.viewmodel.AuthViewModel
import com.monyechi.aistorysculptor.ui.viewmodel.BookDetailsViewModel
import com.monyechi.aistorysculptor.ui.viewmodel.CreateBookViewModel
import com.monyechi.aistorysculptor.ui.viewmodel.LibraryViewModel
import com.monyechi.aistorysculptor.ui.viewmodel.SessionViewModel

@Composable
fun AppNavHost(
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by sessionViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startRoute = if (isLoggedIn == true) Route.MainRoot.value else Route.AuthRoot.value

    NavHost(navController = navController, startDestination = startRoute) {
        navigation(
            route = Route.AuthRoot.value,
            startDestination = Route.Login.value
        ) {
            composable(Route.Login.value) {
                val authViewModel: AuthViewModel = hiltViewModel()
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        sessionViewModel.refreshSession()
                        navController.navigate(Route.MainRoot.value) {
                            popUpTo(Route.AuthRoot.value) { inclusive = true }
                        }
                    },
                    onNavigateRegister = {
                        navController.navigate(Route.Register.value)
                    }
                )
            }

            composable(Route.Register.value) {
                val authViewModel: AuthViewModel = hiltViewModel()
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        sessionViewModel.refreshSession()
                        navController.navigate(Route.MainRoot.value) {
                            popUpTo(Route.AuthRoot.value) { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.popBackStack()
                    }
                )
            }
        }

        navigation(
            route = Route.MainRoot.value,
            startDestination = Route.Library.value
        ) {
            composable(Route.Library.value) {
                val libraryViewModel: LibraryViewModel = hiltViewModel()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Library", style = MaterialTheme.typography.titleLarge) }
                        )
                    }
                ) { padding ->
                    LibraryScreen(
                        paddingValues = padding,
                        viewModel = libraryViewModel,
                        onBookClick = { bookId ->
                            navController.navigate(Route.BookDetails.withId(bookId))
                        },
                        onCreateClick = { navController.navigate(Route.CreateBook.value) },
                        onLogout = {
                            sessionViewModel.logout()
                            navController.navigate(Route.AuthRoot.value) {
                                popUpTo(Route.MainRoot.value) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Route.CreateBook.value) {
                val createBookViewModel: CreateBookViewModel = hiltViewModel()
                CreateBookScreen(
                    viewModel = createBookViewModel,
                    onCreated = { bookId ->
                        navController.navigate(Route.BookDetails.withId(bookId)) {
                            popUpTo(Route.CreateBook.value) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Route.BookDetails.value,
                arguments = listOf(navArgument("bookId") { type = NavType.LongType })
            ) {
                val bookId = it.arguments?.getLong("bookId") ?: 0L
                val bookDetailsViewModel: BookDetailsViewModel = hiltViewModel()
                BookDetailsScreen(
                    bookId = bookId,
                    viewModel = bookDetailsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
