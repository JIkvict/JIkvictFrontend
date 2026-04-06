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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.TeacherStudentControllerApi
import org.jikvict.api.apis.UsersControllerApi
import org.jikvict.api.models.AssignmentInfoAdmin
import org.jikvict.api.models.StatsRequestDto
import org.jikvict.api.models.UserDto
import org.jikvict.browser.components.StudentAssignmentInfo
import org.jikvict.browser.components.StudentStats
import org.jikvict.browser.di.BACKEND_URL
import org.jikvict.browser.util.saveBytesAsFile

class StudentsScreenViewModel(
    private val usersControllerApi: UsersControllerApi,
    private val assignmentControllerApi: AssignmentControllerApi,
    private val teacherStudentControllerApi: TeacherStudentControllerApi,
    private val client: HttpClient
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserDto>>(emptyList())
    val users = _users.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            runCatching {
                val result = usersControllerApi.getAllUsers()
                if (result.success) {
                    _users.value = result.body()
                }
            }
                .onFailure {
                    ensureActive()
                    it.printStackTrace()
                }
        }
    }

    suspend fun getStudentStats(user: UserDto): StudentStats {
        val assignmentsResult = assignmentControllerApi.getAllAdmin()
        if (!assignmentsResult.success) {
            throw Exception("Failed to load assignments: ${assignmentsResult.status}")
        }
        val accessible = user.assignmentGroups.flatMap { it.assignmentIds }.distinct()
        val assignments = assignmentsResult.body().filter { accessible.contains(it.id) }
        val deferredInfos =
            assignments.map { assignment ->
                viewModelScope.async {
                    runCatching {
                        val infoResult =
                            teacherStudentControllerApi.getAssignmentInfo(
                                assignment.id,
                                StatsRequestDto(
                                    userIds = listOf(user.id),
                                    groupIds = emptyList()
                                )
                            )
                        if (infoResult.success) {
                            val infos = infoResult.body()
                            val info = infos.firstOrNull { it.author.id == user.id }
                            if (info != null) {
                                StudentAssignmentInfo(assignment.title, info)
                            } else {
                                StudentAssignmentInfo(
                                    assignment.title,
                                    AssignmentInfoAdmin(
                                        assignmentId = assignment.id,
                                        taskId = assignment.taskId,
                                        maxAttempts =
                                            assignment.maximumAttempts,
                                        attemptsUsed = 0,
                                        results = emptyList(),
                                        unacceptedSubmissions = emptyList(),
                                        author = user
                                    )
                                )
                            }
                        } else {
                            null
                        }
                    }
                        .getOrNull()
                }
            }

        val studentInfos = deferredInfos.awaitAll().filterNotNull()

        val totalAssignments = studentInfos.size
        if (totalAssignments == 0) {
            return StudentStats(emptyList())
        }


        return StudentStats(
            assignments = studentInfos
        )
    }

    fun downloadZipAndSave(assignmentResultId: Long) {
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
                saveBytesAsFile("submission-$assignmentResultId.zip", bytes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
