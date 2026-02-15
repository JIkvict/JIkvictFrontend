package org.jikvict.browser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
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
import org.jikvict.browser.di.BACKEND_URL
import org.jikvict.browser.util.saveBytesAsFile

class AssignmentInfoScreenViewModel(
    val assignmentControllerApi: AssignmentControllerApi,
    val teacherStudentControllerApi: TeacherStudentControllerApi,
    val assignmentGroupControllerApi: AssignmentGroupControllerApi,
    val usersControllerApi: UsersControllerApi,
    private val client: HttpClient,

    ) : ViewModel() {

    private var _assignment = MutableStateFlow<AssignmentDto?>(null)
    val assignment = _assignment.asStateFlow()

    private var _groups = MutableStateFlow<Set<AssignmentGroupDto>?>(null)
    val groups = _groups.asStateFlow()

    private var _users = MutableStateFlow<Set<UserDto>?>(null)
    val users = _users.asStateFlow()

    fun loadGroup(): Job {
        val groups = assignment.value?.assignmentGroupsIds ?: return viewModelScope.launch { }
        println("groups: $groups")
        return viewModelScope.launch {
            runCatching {
                val result = groups.mapNotNull { groupId ->
                    runCatching {
                        assignmentGroupControllerApi.getAssignmentGroupById(groupId)
                    }.onFailure {
                        println("Failed to load group $it: ${it.message}")
                    }.getOrNull()
                }
                _groups.value = result.filter { it.success }.map { it.body() }.toSet()
            }.onFailure {
                ensureActive()
            }
        }
    }


    fun downloadZipAndSave(
        assignmentResultId: Long,
    ) {
        viewModelScope.launch {
            try {
                val url = "$BACKEND_URL/api/teacher/zip/$assignmentResultId"
                val response =
                    client.get(url) {
                        header(HttpHeaders.Accept, Application.OctetStream.toString())
                    }
                if (!response.status.isSuccess()) {
                    return@launch
                }
                val bytes: ByteArray = response.body()
                val ok = saveBytesAsFile("submission-$assignmentResultId.zip", bytes)
            } catch (e: Exception) {
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
                _users.value = result.mapNotNull { if (it.success) it.body() else null }.distinct().toSet()
            }.onFailure {
                ensureActive()
            }
        }
    }


    fun loadAssignments(id: Long): Job {
        return viewModelScope.launch {
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
        if (assignment.value == null) return null
        val extendedGroups = groupIds.ifEmpty {
            assignment.value!!.assignmentGroupsIds
        }
        runCatching {
            val result =
                teacherStudentControllerApi.getAssignmentInfo(
                    assignment.value!!.id,
                    StatsRequestDto(userIds, extendedGroups)
                )
            if (result.success) {
                return result.body()
            }
        }.onFailure {
            return null
        }
        return null
    }
}
