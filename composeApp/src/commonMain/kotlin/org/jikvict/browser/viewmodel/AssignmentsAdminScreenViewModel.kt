package org.jikvict.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.models.AssignmentDto

class AssignmentsAdminScreenViewModel(
    private val assignmentApi: AssignmentControllerApi
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    var _assignments = MutableStateFlow<List<AssignmentDto>>(emptyList())
    val assignments = _assignments.asStateFlow()

    fun loadAssignments() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val resp = assignmentApi.getAllAdmin()
                if (resp.success) {
                    _assignments.value = resp.body()
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