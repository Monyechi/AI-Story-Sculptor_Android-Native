package com.monyechi.aistorysculptor.domain.common

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val message: String, val throwable: Throwable? = null) : AppResult<Nothing>
}
