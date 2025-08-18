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
        _loginResult.value = OperationResult.Loading()
        
        val request = LoginRequest(aisId.value.lowercase(), password.value)
        try {
            val response = loginApi.login(request)
            if (!response.success) {
                _loginResult.value = OperationResult.Error("Server error: ${response.status}")
                return
            }
            _loginResult.value = OperationResult.Success(Unit)
        } catch (e: CancellationException) {
            _loginResult.value = null
            throw e
        } catch (e: ClientRequestException) {
            val problem = e.response.body<ProblemDetail>()
            _loginResult.value = OperationResult.Error(problem.detail ?: "Unknown error")
        } catch (e: ServerResponseException) {
            val problem = e.response.body<ProblemDetail>()
            _loginResult.value = OperationResult.Error(problem.detail ?: "Unknown error")
        } catch (e: Exception) {
            _loginResult.value = OperationResult.Error(e.message ?: "Unknown error")
        }
    }
}
