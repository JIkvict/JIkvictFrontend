package org.jikvict.browser.model

sealed interface OperationResult<T> {
    class Loading<T> : OperationResult<T>

    class Success<T>(
        val result: T,
    ) : OperationResult<T>

    data class Error<T>(
        val message: String,
    ) : OperationResult<T>
}
