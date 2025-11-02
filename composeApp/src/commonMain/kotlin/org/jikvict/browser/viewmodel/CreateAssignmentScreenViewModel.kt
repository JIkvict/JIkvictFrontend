package org.jikvict.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.api.models.CreateAssignmentDto
import org.jikvict.browser.model.OperationResult

class CreateAssignmentScreenViewModel(
    private val assignmentApi: AssignmentControllerApi,
    private val groupApi: AssignmentGroupControllerApi
) : ViewModel() {


    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _availableTasks = MutableStateFlow<List<Long>>(emptyList())
    val availableTasks = _availableTasks.asStateFlow()

    private val _groups = MutableStateFlow<List<AssignmentGroupDto>>(emptyList())
    val groups = _groups.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val resp = assignmentApi.availableTasks()
                if (resp.success) {
                    _availableTasks.value = resp.body()
                } else {
                    _error.value = "Failed to load tasks: ${resp.status}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadGroups() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val resp = groupApi.getAllAssignmentGroups()
                println("Groups: ${resp.body()}")
                if (resp.success) {
                    _groups.value = resp.body()
                } else {
                    _error.value = "Failed to load groups: ${resp.status}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun createAssignment(dto: CreateAssignmentDto): OperationResult<AssignmentDto> {
        return with(viewModelScope) {
            async {
                _loading.value = true
                _error.value = null
                try {
                    val resp = assignmentApi.createAssignment(dto)
                    return@async if (resp.status in 200 until 300) {
                        OperationResult.Success(resp.body())
                    } else {
                        OperationResult.Error("Server error")
                    }
                } catch (e: Exception) {
                    _error.value = e.message ?: "Unknown error"
                    return@async OperationResult.Error(e.message ?: "Couldn't create assignment")
                } finally {
                    _loading.value = false
                }
            }.await()
        }
    }
}