package org.jikvict.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.apis.TeacherStudentControllerApi
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.util.StateSaver

class UserGroupScreenViewModel(
    stateSaver: StateSaver,
    private val teacherApi: TeacherStudentControllerApi,
    private val groupApi: AssignmentGroupControllerApi,
): ViewModel() {


    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _groups = MutableStateFlow<List<AssignmentGroupDto>>(emptyList())
    val groups = _groups.asStateFlow()


    private val _operation = MutableStateFlow<OperationResult<Unit>?>(null)
    val operation = _operation.asStateFlow()

    fun clearOperation() { _operation.value = null }

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
}