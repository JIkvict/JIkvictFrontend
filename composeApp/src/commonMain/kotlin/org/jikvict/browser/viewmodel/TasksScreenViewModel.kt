package org.jikvict.browser.viewmodel

import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.utils.io.core.writeFully
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.api.apis.TaskStatusControllerApi
import org.jikvict.api.infrastructure.ApiClient
import org.jikvict.api.models.AssignmentDto
import org.jikvict.api.models.AssignmentInfo
import org.jikvict.api.models.PendingStatusResponseLong
import org.jikvict.api.models.ProblemDetail
import org.jikvict.browser.screens.AssignmentsUiState
import org.jikvict.browser.util.PickedFile
import org.jikvict.browser.util.StateSaver
import org.jikvict.browser.util.clientConfig
import org.jikvict.browser.util.saveBytesAsFile


enum class SubmissionStatus {
    PASSED,
    FAILED,
    CLOSED,
    OPEN;

    companion object {
        fun from(assignmentInfo: AssignmentInfo, assignment: AssignmentDto): SubmissionStatus {
            return if (assignment.isClosed) {
                when {
                    assignmentInfo.attemptsUsed < 1 -> {
                        FAILED
                    }

                    (assignmentInfo.results.maxOfOrNull { it.points } ?: 0) == 0 -> {
                        FAILED
                    }

                    (assignmentInfo.results.maxOfOrNull { it.points } ?: 0) == assignment.maxPoints -> {
                        PASSED
                    }

                    else -> {
                        CLOSED
                    }
                }
            } else {
                if (assignmentInfo.attemptsUsed < assignment.maximumAttempts) {
                    OPEN
                } else {
                    if ((assignmentInfo.results.maxOfOrNull { it.points } ?: 0) == assignment.maxPoints) {
                        PASSED
                    } else if ((assignmentInfo.results.maxOfOrNull { it.points } ?: 0) == 0) {
                        FAILED
                    } else {
                        CLOSED
                    }
                }
            }
        }
    }
}

class TasksScreenViewModel(
    savedStateHandle: StateSaver,
    private val assignmentControllerApi: AssignmentControllerApi,
    private val taskStatusControllerApi: TaskStatusControllerApi,
) : ExtendedViewModel(savedStateHandle) {
    private val _assignmentsState = MutableStateFlow<AssignmentsUiState>(AssignmentsUiState.Loading)

    val assignmentsState: StateFlow<AssignmentsUiState> = _assignmentsState.asStateFlow()

    val assignments: StateFlow<List<AssignmentDto>> =
        assignmentsState
            .map { state ->
                when (state) {
                    is AssignmentsUiState.Success -> state.assignments.sortedBy { LocalDateTime.parse(it.startDate) }
                    else -> emptyList()
                }
            }.stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    init {
        loadAssignments()

        viewModelScope.launch {
            assignmentsState.collect { state ->
                if (state is AssignmentsUiState.Success) {
                    val assignmentIds = state.assignments.map { it.id }
                    if (assignmentIds.isNotEmpty()) {
                        fetchStatuses(assignmentIds)
                    }
                }
            }
        }
    }


    fun downloadZipAndSave(
        assignmentId: Int,
        onResult: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val client = HttpClient { clientConfig(this) }
                val url = ApiClient.BASE_URL + "/api/assignment/zip/" + assignmentId
                val response =
                    client.get(url) {
                        header(HttpHeaders.Accept, Application.OctetStream.toString())
                    }
                if (!response.status.isSuccess()) {
                    onResult(false)
                    return@launch
                }
                val bytes: ByteArray = response.body()
                val ok = saveBytesAsFile("assignment-$assignmentId.zip", bytes)
                println("Result is: $ok")
                onResult(ok)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun submitSolutionWithPicker(
        assignmentId: Int,
        onStatus: (PendingStatusResponseLong) -> Unit,
        onFinished: (Boolean) -> Unit = {},
        onError: (String) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                val picked =
                    org.jikvict.browser.util
                        .pickFileForUpload()
                if (picked == null) {
                    onFinished(false)
                    return@launch
                }

                submitSolution(picked, assignmentId, onStatus, onFinished, onError)
            } catch (e: Exception) {
                println("Error submitting solution: ${e.message}")
                onFinished(false)
            }
        }
    }

    fun submitSolutionWithFile(
        assignmentId: Int,
        file: PickedFile,
        onStatus: (PendingStatusResponseLong) -> Unit,
        onFinished: (Boolean) -> Unit = {},
        onError: (String) -> Unit = {},
    ) {
        viewModelScope.launch {
            submitSolution(file, assignmentId, onStatus, onFinished, onError)
        }
    }

    private suspend fun submitSolution(
        file: PickedFile,
        assignmentId: Int,
        onStatus: (PendingStatusResponseLong) -> Unit,
        onFinished: (Boolean) -> Unit,
        onError: (String) -> Unit = {},
    ) {
        try {
            val client =
                HttpClient {
                    clientConfig(this)
                }
            val resp =
                client.post(ApiClient.BASE_URL + "/api/v1/solution-checker/submit") {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("file", file.name, Application.OctetStream) {
                                    writeFully(file.bytes)
                                }
                                append("assignmentId", assignmentId.toString())
                            },
                        ),
                    )
                }

            if (resp.status.isSuccess()) {
                val body = resp.body<PendingStatusResponseLong>()
                val statusId = body.data
                if (statusId != null) {
                    onStatus(body)
                    pollStatus(statusId) { onStatus(it) }
                    onFinished(true)
                } else {
                    onFinished(false)
                }
            } else {
                onFinished(false)
            }
        } catch (e: Exception) {
            val message = try {
                when (e) {
                    is ClientRequestException -> {
                        val problemDetail = e.response.body<ProblemDetail>()
                        problemDetail.detail!!
                    }

                    is ServerResponseException -> {
                        val problemDetail = e.response.body<ProblemDetail>()
                        problemDetail.detail!!
                    }

                    else -> {
                        "Unknown error"
                    }
                }
            } catch (_: Exception) {
                "Unknown error"
            }
            onError(message)
            println("Error submitting solution: ${e.message}")
            onFinished(false)
        }
    }

    fun pollStatus(
        statusId: Long,
        onStatus: (PendingStatusResponseLong) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                var current: PendingStatusResponseLong
                do {
                    val resp = taskStatusControllerApi.getTaskStatus(statusId)
                    println("Response: ${resp.body()}")
                    current = resp.body()
                    onStatus(current)
                    if (current.status == PendingStatusResponseLong.Status.PENDING) {
                        delay(1500)
                    }
                } while (current.status == PendingStatusResponseLong.Status.PENDING)
            } catch (e: Exception) {
                println("Error polling status: ${e.message}")
            }
        }
    }

    fun loadAssignments() {
        viewModelScope.launch {
            val state = fetchAssignments()
            _assignmentsState.value = state
        }
    }

    private val _assignmentInfoMap = MutableStateFlow<Map<Int, AssignmentInfo>>(emptyMap())
    val assignmentInfoMap: StateFlow<Map<Int, AssignmentInfo>> = _assignmentInfoMap.asStateFlow()

    private suspend fun fetchStatuses(ids: List<Long>) {
        try {
            println("Fetching assignment statuses for ids: $ids")
            val responses = ids.map { id ->
                assignmentControllerApi.getAssignmentInfoForUser(id)
            }
            val infoMap = mutableMapOf<Int, AssignmentInfo>()
            responses.forEach { response ->
                if (response.success) {
                    val info = response.body()
                    infoMap[info.assignmentId.toInt()] = info
                }
            }

            _assignmentInfoMap.value = infoMap
        } catch (e: Exception) {
            println("Error fetching assignment statuses in background: ${e.message}")
        }
    }

    fun refreshAssignments() {
        _assignmentsState.value = AssignmentsUiState.Loading
        loadAssignments()
    }

    /**
     * Get assignment info by assignment ID for convenient access
     */
    fun getAssignmentInfo(assignmentId: Int): AssignmentInfo? {
        return _assignmentInfoMap.value[assignmentId]
    }

    private suspend fun fetchAssignments(): AssignmentsUiState {
        return try {
            val response = assignmentControllerApi.getAll()

            if (!response.success) {
                return AssignmentsUiState.Error("Server error: ${response.status}")
            }

            val assignments = response.body()

            if (assignments.isEmpty()) {
                return AssignmentsUiState.Success(emptyList())
            }


            AssignmentsUiState.Success(
                assignments = assignments,
            )
        } catch (e: Exception) {
            AssignmentsUiState.Error("Error: ${e.message}")
        }
    }
}
