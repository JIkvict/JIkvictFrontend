package org.jikvict.browser.viewmodel

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jikvict.api.apis.AuthControllerApi
import org.jikvict.api.models.LoginRequest
import org.jikvict.api.models.ProblemDetail
import org.jikvict.browser.delegates.stateHandle
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.util.StateSaver

class LoginScreenViewModel(
    stateSaver: StateSaver,
    private val loginApi: AuthControllerApi,
) : ExtendedViewModel(stateSaver) {
    private val _aisId = stateHandle("aisId", "")
    val aisId = _aisId.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun setAisId(id: String) {
        _aisId.set(id)
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    private val _loginResult = MutableStateFlow<OperationResult<Unit>?>(null)
    val loginResult = _loginResult.asStateFlow()

    fun resetLoginResult() {
        _loginResult.value = null
    }

    suspend fun login() {
        val request = LoginRequest(aisId.value.lowercase(), password.value)
        try {
            val response = loginApi.login(request)
            val token = response.body()
            println("Token is: $token")
            OperationResult.Success(Unit).also { _loginResult.value = it }
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            val problem = e.response.body<ProblemDetail>()
            OperationResult.Error<Unit>(problem.detail ?: "Unknown error").also { _loginResult.value = it }
        } catch (e: ServerResponseException) {
            val problem = e.response.body<ProblemDetail>()
            OperationResult.Error<Unit>(problem.detail ?: "Unknown error").also { _loginResult.value = it }
        } catch (e: Exception) {
            OperationResult.Error<Unit>(e.message ?: "Unknown error").also { _loginResult.value = it }
        }
    }
}
