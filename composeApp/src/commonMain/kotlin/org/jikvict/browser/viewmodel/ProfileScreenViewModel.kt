package org.jikvict.browser.viewmodel

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jikvict.api.apis.LongLivingTokenControllerApi
import org.jikvict.api.models.ProblemDetail
import org.jikvict.browser.auth.TokenHolder
import org.jikvict.browser.auth.toJwt
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.util.StateSaver

class ProfileScreenViewModel(
    stateSaver: StateSaver,
    private val tokenApi: LongLivingTokenControllerApi,
) : ExtendedViewModel(stateSaver) {
    
    private val _longLivingToken = MutableStateFlow<String?>(null)
    val longLivingToken = _longLivingToken.asStateFlow()
    
    private val _tokenLoadResult = MutableStateFlow<OperationResult<Unit>?>(null)
    val tokenLoadResult = _tokenLoadResult.asStateFlow()
    
    private val _tokenCreateResult = MutableStateFlow<OperationResult<Unit>?>(null)
    val tokenCreateResult = _tokenCreateResult.asStateFlow()
    
    private val _tokenDeleteResult = MutableStateFlow<OperationResult<Unit>?>(null)
    val tokenDeleteResult = _tokenDeleteResult.asStateFlow()
    
    val userName: String
        get() = TokenHolder.token()?.toJwt()?.sub ?: "User"
    
    fun resetTokenLoadResult() {
        _tokenLoadResult.value = null
    }
    
    fun resetTokenCreateResult() {
        _tokenCreateResult.value = null
    }
    
    fun resetTokenDeleteResult() {
        _tokenDeleteResult.value = null
    }
    
    suspend fun loadToken() {
        _tokenLoadResult.value = OperationResult.Loading()
        try {
            val response = tokenApi.getLongLivingToken()
            if (!response.success) {
                _tokenLoadResult.value = OperationResult.Error("Failed to load token")
                _longLivingToken.value = null
                return
            }
            _longLivingToken.value = response.body().accessToken
            _tokenLoadResult.value = OperationResult.Success(Unit)
        } catch (e: CancellationException) {
            _tokenLoadResult.value = null
            throw e
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 404) {
                _longLivingToken.value = null
                _tokenLoadResult.value = OperationResult.Success(Unit)
            } else {
                runCatching {
                    val problem = e.response.body<ProblemDetail>()
                    _tokenLoadResult.value = OperationResult.Error(problem.detail ?: "Unknown error")
                }
            }
        } catch (e: ServerResponseException) {
            runCatching {
                val problem = e.response.body<ProblemDetail>()
                _tokenLoadResult.value = OperationResult.Error(problem.detail ?: "Unknown error")
            }
        } catch (e: Exception) {
            _tokenLoadResult.value = OperationResult.Error("Unknown error")
            println("Exception occurred")
            println(e.message)
        }
    }
    
    suspend fun createToken() {
        _tokenCreateResult.value = OperationResult.Loading()
        try {
            val response = tokenApi.createLongLivingToken()
            if (!response.success) {
                _tokenCreateResult.value = OperationResult.Error("Failed to create token")
                return
            }
            _longLivingToken.value = response.body().accessToken
            _tokenCreateResult.value = OperationResult.Success(Unit)
        } catch (e: CancellationException) {
            _tokenCreateResult.value = null
            throw e
        } catch (e: ClientRequestException) {
            runCatching {
                val problem = e.response.body<ProblemDetail>()
                _tokenCreateResult.value = OperationResult.Error(problem.detail ?: "Unknown error")
            }
        } catch (e: ServerResponseException) {
            runCatching {
                val problem = e.response.body<ProblemDetail>()
                _tokenCreateResult.value = OperationResult.Error(problem.detail ?: "Unknown error")
            }
        } catch (e: Exception) {
            _tokenCreateResult.value = OperationResult.Error("Unknown error")
            println("Exception occurred")
            println(e.message)
        }
    }
    
    suspend fun deleteToken() {
        _tokenDeleteResult.value = OperationResult.Loading()
        try {
            val response = tokenApi.deleteLongLivingToken()
            if (!response.success) {
                _tokenDeleteResult.value = OperationResult.Error("Failed to delete token")
                return
            }
            _longLivingToken.value = null
            _tokenDeleteResult.value = OperationResult.Success(Unit)
        } catch (e: CancellationException) {
            _tokenDeleteResult.value = null
            throw e
        } catch (e: ClientRequestException) {
            runCatching {
                val problem = e.response.body<ProblemDetail>()
                _tokenDeleteResult.value = OperationResult.Error(problem.detail ?: "Unknown error")
            }
        } catch (e: ServerResponseException) {
            runCatching {
                val problem = e.response.body<ProblemDetail>()
                _tokenDeleteResult.value = OperationResult.Error(problem.detail ?: "Unknown error")
            }
        } catch (e: Exception) {
            _tokenDeleteResult.value = OperationResult.Error("Unknown error")
            println("Exception occurred")
            println(e.message)
        }
    }
}
