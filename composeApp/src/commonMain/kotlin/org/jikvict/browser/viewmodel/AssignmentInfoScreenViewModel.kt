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
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.AssignmentGroupControllerApi
import org.jikvict.api.apis.PlagiarismControllerApi
import org.jikvict.api.apis.TeacherStudentControllerApi
import org.jikvict.api.apis.UsersControllerApi
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentGroupDto
import org.jikvict.api.models.AssignmentInfoAdmin
import org.jikvict.api.models.PlagiarismCheckParameters
import org.jikvict.api.models.PlagiarismCheckSummaryResponse
import org.jikvict.api.models.StatsRequestDto
import org.jikvict.api.models.UserDto
import org.jikvict.browser.di.BACKEND_URL
import org.jikvict.browser.util.openExternalUrl
import org.jikvict.browser.util.openJPlagViewerWithReport
import org.jikvict.browser.util.saveBytesAsFile

class AssignmentInfoScreenViewModel(
    val assignmentControllerApi: AssignmentControllerApi,
    val teacherStudentControllerApi: TeacherStudentControllerApi,
    val assignmentGroupControllerApi: AssignmentGroupControllerApi,
    val usersControllerApi: UsersControllerApi,
    val plagiarismControllerApi: PlagiarismControllerApi,
    private val client: HttpClient,

    ) : ViewModel() {

    private val _plagiarismChecks = MutableStateFlow<List<PlagiarismCheckSummaryResponse>>(emptyList())
    val plagiarismChecks = _plagiarismChecks.asStateFlow()

    private val _plagiarismLoading = MutableStateFlow(false)
    val plagiarismLoading = _plagiarismLoading.asStateFlow()

    private val _plagiarismStarting = MutableStateFlow(false)
    val plagiarismStarting = _plagiarismStarting.asStateFlow()

    private var pollingJob: Job? = null
    private val pollIntervalMs: Long = 5_000

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
                val result = runCatching {
                    usersControllerApi.getUsersByIds(ids).body()
                }.getOrNull() ?: return@launch
                _users.value = result.toSet()
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

    fun loadPlagiarismChecks(): Job {
        val assignmentId = assignment.value?.id ?: return viewModelScope.launch { }
        return viewModelScope.launch {
            _plagiarismLoading.value = true
            runCatching {
                val response = plagiarismControllerApi.listChecks(assignmentId)
                if (response.success) {
                    _plagiarismChecks.value = response.body()
                }
            }.onFailure {
                ensureActive()
                it.printStackTrace()
            }
            _plagiarismLoading.value = false
            schedulePollingIfNeeded()
        }
    }

    private fun schedulePollingIfNeeded() {
        val hasPending = _plagiarismChecks.value.any {
            it.status == PlagiarismCheckSummaryResponse.Status.PENDING
        }
        pollingJob?.cancel()
        pollingJob = if (hasPending) {
            viewModelScope.launch {
                delay(pollIntervalMs)
                loadPlagiarismChecks()
            }
        } else {
            null
        }
    }

    fun startPlagiarismCheck(parameters: PlagiarismCheckParameters? = null): Job {
        val assignmentId = assignment.value?.id ?: return viewModelScope.launch { }
        return viewModelScope.launch {
            _plagiarismStarting.value = true
            runCatching {
                plagiarismControllerApi.startCheck(assignmentId, parameters)
            }.onFailure {
                ensureActive()
                it.printStackTrace()
            }
            _plagiarismStarting.value = false
            loadPlagiarismChecks().join()
        }
    }

    override fun onCleared() {
        pollingJob?.cancel()
        pollingJob = null
        super.onCleared()
    }

    private suspend fun fetchPlagiarismReportBytes(taskId: Long): ByteArray? {
        return try {
            val url = "$BACKEND_URL/api/admin/plagiarism/check/$taskId/report"
            val response = client.get(url) {
                header(HttpHeaders.Accept, Application.OctetStream.toString())
            }
            if (!response.status.isSuccess()) return null
            response.body<ByteArray>()
        } catch (e: kotlin.coroutines.cancellation.CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun downloadPlagiarismReport(taskId: Long) {
        viewModelScope.launch {
            val bytes = fetchPlagiarismReportBytes(taskId) ?: return@launch
            saveBytesAsFile("plagiarism-report-$taskId.zip", bytes)
        }
    }

    fun viewPlagiarismReport(taskId: Long) {
        viewModelScope.launch {
            val bytes = fetchPlagiarismReportBytes(taskId)
            if (bytes != null) {
                openJPlagViewerWithReport(
                    fileName = "plagiarism-report-$taskId.zip",
                    bytes = bytes,
                )
            } else {
                openExternalUrl("https://jplag.github.io/JPlag/", true)
            }
        }
    }

    suspend fun loadInfos(groupIds: List<Long>, userIds: List<Long>): List<AssignmentInfoAdmin>? {
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
