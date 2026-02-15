package com.monyechi.aistorysculptor.domain.usecase

import com.monyechi.aistorysculptor.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) = repo.login(email, password)
}

class RegisterUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String, displayName: String) =
        repo.register(username, email, password, displayName)
}

class LogoutUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke() = repo.logout()
}

class IsLoggedInUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke() = repo.isLoggedIn()
}

class GetCurrentUserUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke() = repo.getCurrentUser()
}

class GetTokenBalanceUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke() = repo.getTokenBalance()
}

class AddTokensUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(amount: Int) = repo.addTokens(amount)
}

class SubtractTokensUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(amount: Int) = repo.subtractTokens(amount)
}

class DeleteAccountUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke() = repo.deleteAccount()
}
