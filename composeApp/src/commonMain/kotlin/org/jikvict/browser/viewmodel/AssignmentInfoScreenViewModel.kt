package org.jikvict.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.apis.TeacherStudentControllerApi
import org.jikvict.api.apis.UsersControllerApi
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.api.models.AssignmentInfo
import org.jikvict.api.models.StatsRequestDto
import org.jikvict.api.models.UserDto

class AssignmentInfoScreenViewModel(
    val assignmentControllerApi: AssignmentControllerApi,
    val teacherStudentControllerApi: TeacherStudentControllerApi,
    val assignmentGroupControllerApi: AssignmentGroupControllerApi,
    val usersControllerApi: UsersControllerApi
) : ViewModel() {

    private var _assignment = MutableStateFlow<AssignmentDto?>(null)
    val assignment = _assignment.asStateFlow()

    private var _groups = MutableStateFlow<List<AssignmentGroupDto>?>(null)
    val groups = _groups.asStateFlow()

    private var _users = MutableStateFlow<List<UserDto>?>(null)
    val users = _users.asStateFlow()

    fun loadGroup(): Job {
        return viewModelScope.launch {
            runCatching {
                val result = assignmentGroupControllerApi.getAllAssignmentGroups()
                if (result.success) {
                    _groups.value = result.body()
                }
            }.onFailure {
                ensureActive()
            }
        }
    }

    fun loadUsers() {
        val ids = groups.value?.flatMap { it.userIds } ?: return
        viewModelScope.launch {
            runCatching {
                val result = ids.mapNotNull {
                    runCatching {
                        usersControllerApi.getUserById(it)
                    }.getOrNull()
                }
                println("Users are: ${result.map { it.body() }}")
                _users.value = result.mapNotNull { if (it.success) it.body() else null }.distinct()
            }.onFailure {
                ensureActive()
            }
        }
    }


    fun loadAssignments(id: Long) {
        viewModelScope.launch {
            runCatching {
                val result = assignmentControllerApi.getAssignmentAdmin(id)
                if (result.success) {
                    _assignment.value = result.body()
                }
            }.onFailure {
                ensureActive()
            }
        }
    }

    suspend fun loadInfos(groupIds: List<Long>, userIds: List<Long>): List<AssignmentInfo>? {
        println("I was called to fetch stats for users: $userIds and groups: $groupIds")
        if (assignment.value == null) return null

        runCatching {
            val result =
                teacherStudentControllerApi.getAssignmentInfo(assignment.value!!.id, StatsRequestDto(userIds, groupIds))
            println("Success body is: ${result.body()}")
            if (result.success) {
                return result.body()
            }
        }.onFailure {
            println("Failed to load assignment: ${it.message}")
            return null
        }
        return null
    }
}
