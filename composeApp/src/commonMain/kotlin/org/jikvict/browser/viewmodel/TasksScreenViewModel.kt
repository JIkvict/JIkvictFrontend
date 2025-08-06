package org.jikvict.browser.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jikvict.api.apis.AssignmentControllerApi
import org.jikvict.browser.screens.Assignment
import org.jikvict.browser.screens.AssignmentsUiState
import org.jikvict.browser.util.StateSaver

class TasksScreenViewModel(
    savedStateHandle: StateSaver,
    private val assignmentControllerApi: AssignmentControllerApi,
) : ExtendedViewModel(savedStateHandle) {
    private val _assignmentsState = MutableStateFlow<AssignmentsUiState>(AssignmentsUiState.Loading)

    val assignmentsState: StateFlow<AssignmentsUiState> = _assignmentsState.asStateFlow()

    val assignments: StateFlow<List<Assignment>> =
        assignmentsState
            .map { state ->
                when (state) {
                    is AssignmentsUiState.Success -> state.assignments
                    else -> emptyList()
                }
            }.stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    val isLoading: StateFlow<Boolean> =
        assignmentsState
            .map { state ->
                state is AssignmentsUiState.Loading
            }.stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = true,
            )

    val isLoadingMore: StateFlow<Boolean> =
        assignmentsState
            .map { state ->
                (state as? AssignmentsUiState.Success)?.isLoadingMore ?: false
            }.stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = false,
            )

    val hasMorePages: StateFlow<Boolean> =
        assignmentsState
            .map { state ->
                (state as? AssignmentsUiState.Success)?.hasMorePages ?: false
            }.stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = false,
            )

    init {
        loadAssignments()
    }

    fun loadAssignments() {
        viewModelScope.launch {
            val state = fetchAssignments(page = 0)
            _assignmentsState.value = state
        }
    }

    fun refreshAssignments() {
        _assignmentsState.value = AssignmentsUiState.Loading
        loadAssignments()
    }

    fun loadMoreAssignments() {
        val currentState = _assignmentsState.value

        if (currentState is AssignmentsUiState.Success &&
            currentState.hasMorePages &&
            !currentState.isLoadingMore
        ) {
            _assignmentsState.value = currentState.copy(isLoadingMore = true)

            viewModelScope.launch {
                val newState = loadMoreAssignments(currentState)
                _assignmentsState.value = newState
            }
        }
    }

    private suspend fun fetchAssignments(
        page: Int = 0,
        pageSize: Int = 20,
    ): AssignmentsUiState {
        return try {
            val response = assignmentControllerApi.getAll(page = page, size = pageSize)

            if (!response.success) {
                return AssignmentsUiState.Error("Server error: ${response.status}")
            }

            val body = response.body()
            val content = body.content
            val pageMetadata = body.page

            if (content == null) {
                return AssignmentsUiState.Success(emptyList())
            }

            if (content.isEmpty()) {
                return AssignmentsUiState.Success(emptyList())
            }

            val totalPages = pageMetadata?.totalPages?.toInt() ?: 0
            val currentPage = pageMetadata?.number?.toInt() ?: 0
            val hasMorePages = currentPage < totalPages - 1

            val assignments =
                content.mapIndexed { index, dto ->
                    Assignment(
                        id = (page * pageSize) + index,
                        title = dto.title,
                        description = dto.description ?: "No description",
                        taskNumber = dto.taskId,
                    )
                }

            AssignmentsUiState.Success(
                assignments = assignments,
                currentPage = currentPage,
                hasMorePages = hasMorePages,
                isLoadingMore = false,
            )
        } catch (e: Exception) {
            AssignmentsUiState.Error("Error: ${e.message}")
        }
    }

    private suspend fun loadMoreAssignments(currentState: AssignmentsUiState.Success): AssignmentsUiState {
        if (!currentState.hasMorePages || currentState.isLoadingMore) {
            return currentState
        }

        val nextPage = currentState.currentPage + 1

        return try {
            val response = assignmentControllerApi.getAll(page = nextPage, size = 20)

            if (!response.success) {
                return AssignmentsUiState.Error("Server error: ${response.status}")
            }

            val body = response.body()
            val content = body.content
            val pageMetadata = body.page

            if (content == null || content.isEmpty()) {
                return currentState.copy(hasMorePages = false, isLoadingMore = false)
            }

            val totalPages = pageMetadata?.totalPages?.toInt() ?: 0
            val currentPageFromResponse = pageMetadata?.number?.toInt() ?: 0
            val hasMorePages = currentPageFromResponse < totalPages - 1

            val newAssignments =
                content.mapIndexed { index, dto ->
                    Assignment(
                        id = (nextPage * 20) + index,
                        title = dto.title,
                        description = dto.description ?: "No description",
                        taskNumber = dto.taskId,
                    )
                }

            val combinedAssignments = currentState.assignments + newAssignments

            AssignmentsUiState.Success(
                assignments = combinedAssignments,
                currentPage = nextPage,
                hasMorePages = hasMorePages,
                isLoadingMore = false,
            )
        } catch (_: Exception) {
            currentState.copy(isLoadingMore = false)
        }
    }
}
