package org.jikvict.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.browser.model.OperationResult
import kotlin.coroutines.cancellation.CancellationException

class EditAssignmentScreenViewModel(
    private val assignmentControllerApi: AssignmentControllerApi,
    private val assignmentGroupControllerApi: AssignmentGroupControllerApi,
) : ViewModel() {

    private var _assignment = MutableStateFlow<AssignmentDto?>(null)
    val assignment = _assignment.asStateFlow()

    private var _assignmentGroups = MutableStateFlow<List<AssignmentGroupDto>>(emptyList())
    val assignmentGroups = _assignmentGroups.asStateFlow()

    private var _tasks = MutableStateFlow<List<Long>>(emptyList())
    val tasks = _tasks.asStateFlow()

    fun loadAssignment(assignmentId: Long) {
        viewModelScope.launch {
            runCatching {
                val result = assignmentControllerApi.getAssignmentAdmin(assignmentId)
                if (result.success) {
                    println("Success body is: ${result.body()}")
                    _assignment.value = result.body()
                } else {
                    println("Failed to load assignment: ${result.status}")
                    _assignment.value = null
                }
            }.onFailure {
                println("Failed to load assignment: ${it.message}")
                _assignment.value = null
                ensureActive()
            }
        }
    }

    fun loadGroups() {
        viewModelScope.launch {
            runCatching {
                val result = assignmentGroupControllerApi.getAllAssignmentGroups()
                if (result.success) {
                    _assignmentGroups.value = result.body()
                }
            }.onFailure {
                ensureActive()
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            runCatching {
                val result = assignmentControllerApi.availableTasks()
                if (result.success) {
                    _tasks.value = result.body()
                }
            }.onFailure {
                ensureActive()
            }
        }
    }

    suspend fun update(newAssignment: AssignmentDto): OperationResult<AssignmentDto> {
        val current = assignment.value ?: return OperationResult.Error("No assignment to update")
        if (current == newAssignment) return OperationResult.Success(newAssignment)

        return try {
            val response = assignmentControllerApi.updateAssignment(current.id, newAssignment)
            if (response.success) {
                val body = response.body()
                _assignment.value = body
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