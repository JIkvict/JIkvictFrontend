package org.jikvict.browser.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.apis.TeacherStudentControllerApi
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.api.models.StudentOverviewDto
import org.jikvict.api.models.UpdatePointsRequest
import org.jikvict.browser.delegates.stateHandle
import org.jikvict.browser.model.OperationResult
import org.jikvict.browser.util.StateSaver

class AdminScreenViewModel(
    stateSaver: StateSaver,
    private val teacherApi: TeacherStudentControllerApi,
    private val groupApi: AssignmentGroupControllerApi,
) : ExtendedViewModel(stateSaver) {

    private val _groups = MutableStateFlow<List<AssignmentGroupDto>>(emptyList())
    val groups = _groups.asStateFlow()

    private val _selectedGroupId = stateHandle("admin_selected_group", -1L)
    val selectedGroupId = _selectedGroupId.asStateFlow()

    private val _selectedUserId = stateHandle("admin_selected_user", -1L)
    val selectedUserId = _selectedUserId.asStateFlow()

    private val _overview = MutableStateFlow<StudentOverviewDto?>(null)
    val overview = _overview.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

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

    fun selectGroup(id: Long) {
        _selectedGroupId.set(id)
        // reset user and overview when group changes
        _selectedUserId.set(-1L)
        _overview.value = null
    }

    fun selectUser(id: Long) {
        _selectedUserId.set(id)
        loadOverview(id)
    }

    fun loadOverview(userId: Long = selectedUserId.value) {
        if (userId <= 0) return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val resp = teacherApi.getStudentOverview(userId)
                if (resp.success) {
                    _overview.value = resp.body()
                } else {
                    _error.value = "Failed to load overview: ${'$'}{resp.status}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteSubmission(submissionId: Long) {
        viewModelScope.launch {
            _operation.value = OperationResult.Loading()
            try {
                val resp = teacherApi.deleteSubmission(submissionId)
                if (resp.success) {
                    _operation.value = OperationResult.Success(Unit)
                    // refresh overview after deletion
                    loadOverview()
                } else {
                    _operation.value = OperationResult.Error("Delete failed: ${'$'}{resp.status}")
                }
            } catch (e: Exception) {
                _operation.value = OperationResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updatePoints(resultId: Long, points: Int) {
        viewModelScope.launch {
            _operation.value = OperationResult.Loading()
            try {
                val resp = teacherApi.updateResultPoints(resultId, UpdatePointsRequest(points))
                if (resp.success) {
                    _operation.value = OperationResult.Success(Unit)
                    // refresh overview after update
                    loadOverview()
                } else {
                    _operation.value = OperationResult.Error("Update failed: ${'$'}{resp.status}")
                }
            } catch (e: Exception) {
                _operation.value = OperationResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Helpers for statistics
    data class Stats(
        val usersInGroup: Int = 0,
        val submissionsTotal: Int = 0,
        val submissionsDone: Int = 0,
        val submissionsPending: Int = 0,
        val submissionsFailed: Int = 0,
        val submissionsRejected: Int = 0,
        val resultsCount: Int = 0,
        val totalPoints: Int = 0,
    )

    val stats = MutableStateFlow(Stats())

    fun recomputeStats() {
        val groupUsers = groups.value.firstOrNull { it.id == selectedGroupId.value }?.userIds?.size ?: 0
        val ov = overview.value
        val subs = ov?.submissions ?: emptyList()
        val results = ov?.results ?: emptyList()
        val s = Stats(
            usersInGroup = groupUsers,
            submissionsTotal = subs.size,
            submissionsDone = subs.count { it.status.name == "DONE" },
            submissionsPending = subs.count { it.status.name == "PENDING" },
            submissionsFailed = subs.count { it.status.name == "FAILED" },
            submissionsRejected = subs.count { it.status.name == "REJECTED" },
            resultsCount = results.size,
            totalPoints = results.sumOf { it.points },
        )
        stats.value = s
    }
}