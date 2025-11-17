package org.jikvict.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.apis.UsersControllerApi
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.api.models.UserDto
import org.jikvict.browser.model.OperationResult
import kotlin.coroutines.cancellation.CancellationException

class EditAssignmentGroupViewModel(
    private val assignmentGroupControllerApi: AssignmentGroupControllerApi,
    private val usersControllerApi: UsersControllerApi,
    private val assignmentControllerApi: AssignmentControllerApi
) : ViewModel() {

    private var _group = MutableStateFlow<AssignmentGroupDto?>(null)
    val group = _group.asStateFlow()

    private var _users = MutableStateFlow<List<UserDto>>(emptyList())
    val users = _users.asStateFlow()

    private var _assignments = MutableStateFlow<List<AssignmentDto>>(emptyList())
    val assignments = _assignments.asStateFlow()

    fun loadAssignments() {
        group.value?.id ?: return
        viewModelScope.launch {
            runCatching {
                val result = assignmentControllerApi.getAllForAssignmentGroup(group.value!!.id!!)
                if (result.success) {
                    _assignments.value = result.body()
                }
            }.onFailure {
                ensureActive()
            }
        }
    }

    fun loadGroup(assignmentId: Long) {
        viewModelScope.launch {
            runCatching {
                val result = assignmentGroupControllerApi.getAssignmentGroupById(assignmentId)
                if (result.success) {
                    _group.value = result.body()
                    loadAssignments()
                }
            }.onFailure {
                ensureActive()
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            runCatching {
                val result = usersControllerApi.getAllUsers()
                if (result.success) {
                    _users.value = result.body()
                }
            }.onFailure {
                ensureActive()
            }
        }
    }

    fun clearGroup() {
        _group.value = null
    }

    suspend fun update(newGroup: AssignmentGroupDto): OperationResult<AssignmentGroupDto> {
        val current = group.value ?: return OperationResult.Error("No assignment to update")
        if (current == newGroup) return OperationResult.Success(newGroup)
        if (current.id == null) return OperationResult.Error("No assignment group id")

        return try {
            val response = assignmentGroupControllerApi.updateAssignmentGroup(current.id, newGroup)
            if (response.success) {
                val body = response.body()
                _group.value = body
                OperationResult.Success(body)
            } else {
                OperationResult.Error("Failed to update assignment")
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (t: Throwable) {
            OperationResult.Error(t.message ?: "Unknown error")
        }
    }
}