package com.monyechi.aistorysculptor.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monyechi.aistorysculptor.R
import com.monyechi.aistorysculptor.ui.common.AppScaffold
import com.monyechi.aistorysculptor.ui.common.BannerButton
import com.monyechi.aistorysculptor.ui.common.DarkContainer
import com.monyechi.aistorysculptor.ui.common.OliveButton
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.theme.*
import com.monyechi.aistorysculptor.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    LoginScreenContent(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onLoginClick = { authViewModel.login(email, password, onLoginSuccess) },
        onSignUpClick = onNavigateRegister,
        authState = authState,
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AIStorySculptorTheme {
        LoginScreenContent(
            email = "user@example.com",
            password = "password",
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignUpClick = {},
            authState = null,
        )
    }
}

@Composable
private fun LoginScreenContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    authState: UiState<*>?,
) {
    AppScaffold(backgroundRes = R.drawable.background_hero) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.aiss_logo),
                contentDescription = "AI Story Sculptor Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "AI Story Sculptor",
                style = MaterialTheme.typography.headlineLarge,
                color = White,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Craft captivating stories with the power of AI",
                style = MaterialTheme.typography.bodyMedium,
                color = White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            DarkContainer(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 15.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.headlineSmall,
                        color = White,
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = OliveAccent,
                            unfocusedBorderColor = Beige.copy(alpha = 0.5f),
                            focusedLabelColor = OliveAccent,
                            unfocusedLabelColor = Beige.copy(alpha = 0.7f),
                            cursorColor = OliveAccent,
                        ),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = OliveAccent,
                            unfocusedBorderColor = Beige.copy(alpha = 0.5f),
                            focusedLabelColor = OliveAccent,
                            unfocusedLabelColor = Beige.copy(alpha = 0.7f),
                            cursorColor = OliveAccent,
                        ),
                        singleLine = true,
                    )

                    when (authState) {
                        is UiState.Error -> Text(
                            authState.message,
                            color = DangerRed,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        UiState.Loading -> CircularProgressIndicator(color = AccentGreen)
                        is UiState.Success -> Unit
                        null -> Unit
                    }

                    BannerButton(
                        text = "Login",
                        onClick = onLoginClick,
                    )

                    TextButton(onClick = onSignUpClick) {
                        Text(
                            "Don't have an account? Sign up",
                            color = Beige,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}
