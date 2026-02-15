package com.monyechi.aistorysculptor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monyechi.aistorysculptor.domain.common.AppResult
import com.monyechi.aistorysculptor.domain.usecase.LoginUseCase
import com.monyechi.aistorysculptor.domain.usecase.RegisterUseCase
import com.monyechi.aistorysculptor.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val authState: StateFlow<UiState<Unit>> = _authState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            when (val result = loginUseCase(email, password)) {
                is AppResult.Success -> {
                    _authState.value = UiState.Success(Unit)
                    onSuccess()
                }

                is AppResult.Failure -> {
                    _authState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun register(email: String, password: String, displayName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            when (val result = registerUseCase(email, password, displayName)) {
                is AppResult.Success -> {
                    _authState.value = UiState.Success(Unit)
                    onSuccess()
                }

                is AppResult.Failure -> {
                    _authState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun clearError() {
        _authState.value = UiState.Success(Unit)
    }
}
