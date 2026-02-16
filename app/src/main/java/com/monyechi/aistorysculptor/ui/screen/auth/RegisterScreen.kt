package com.monyechi.aistorysculptor.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monyechi.aistorysculptor.R
import com.monyechi.aistorysculptor.ui.common.AppScaffold
import com.monyechi.aistorysculptor.ui.common.DarkContainer
import com.monyechi.aistorysculptor.ui.common.GreenButton
import com.monyechi.aistorysculptor.ui.common.DangerButton
import com.monyechi.aistorysculptor.ui.common.UiState
import com.monyechi.aistorysculptor.ui.theme.*
import com.monyechi.aistorysculptor.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = White,
        unfocusedTextColor = White,
        focusedBorderColor = OliveAccent,
        unfocusedBorderColor = Beige.copy(alpha = 0.5f),
        focusedLabelColor = OliveAccent,
        unfocusedLabelColor = Beige.copy(alpha = 0.7f),
        cursorColor = OliveAccent,
    )

    AppScaffold(backgroundRes = R.drawable.background_hero) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Join AI Story Sculptor",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
            )

            Spacer(Modifier.height(24.dp))

            DarkContainer(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 15.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineSmall,
                        color = White,
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = fieldColors,
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = fieldColors,
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = fieldColors,
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = fieldColors,
                        singleLine = true,
                    )

                    when (val state = authState) {
                        is UiState.Error -> Text(
                            state.message,
                            color = DangerRed,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        UiState.Loading -> CircularProgressIndicator(color = AccentGreen)
                        is UiState.Success -> Unit
                    }

                    GreenButton(
                        text = "Sign Up",
                        onClick = {
                            authViewModel.register(username, email, password, displayName, onRegisterSuccess)
                        },
                    )

                    TextButton(onClick = onBackToLogin) {
                        Text(
                            "Already have an account? Login",
                            color = Beige,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}
