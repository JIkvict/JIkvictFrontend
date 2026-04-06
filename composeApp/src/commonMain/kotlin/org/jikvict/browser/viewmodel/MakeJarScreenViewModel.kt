package org.jikvict.browser.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.jikvict.browser.util.PublicHttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.constant.ThemeColors

@Serializable
data class GitHubRelease(
    @SerialName("published_at") val publishedAt: String,
)

class MakeJarScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val publicHttpClient: PublicHttpClient,
) : ViewModel() {
    private val _hasNewPluginRelease = MutableStateFlow(false)
    val hasNewPluginRelease = _hasNewPluginRelease.asStateFlow()

    init {
        viewModelScope.launch {
            checkForNewPluginRelease()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun checkForNewPluginRelease() {
        try {
            val releases = publicHttpClient.client.get("https://api.github.com/repos/JIkvict/JIkvictIdeaPlugin/releases") {
                header(HttpHeaders.Accept, "application/vnd.github+json")
            }.body<List<GitHubRelease>>()
            val latest = releases.firstOrNull() ?: return
            val publishedAt = latest.publishedAt
            val releaseInstant = Instant.parse(publishedAt)
            val now = Clock.System.now()
            _hasNewPluginRelease.value = (now - releaseInstant) < 7.days
        } catch (_: Exception) {
            _hasNewPluginRelease.value = false
        }
    }
    private val _jarWarOffsetY = MutableStateFlow(0)
    val jarWarOffsetY = _jarWarOffsetY.asStateFlow()

    private val _jarWarHeightPx = MutableStateFlow(0)
    val jarWarHeightPx = _jarWarHeightPx.asStateFlow()

    private val _solveTestCreatePosition = MutableStateFlow(0)
    val solveTestCreatePosition = _solveTestCreatePosition.asStateFlow()

    private val _animationProgress = MutableStateFlow(0f)
    val animationProgress = _animationProgress.asStateFlow()

    private val _purpleColor = MutableStateFlow<Color>(Color.Unspecified)
    val purpleColor = _purpleColor.asStateFlow()

    private val _redColor = MutableStateFlow<Color>(Color.Unspecified)
    val redColor = _redColor.asStateFlow()

    fun updateJarWarHeightPx(height: Int) {
        _jarWarHeightPx.value = height
    }

    fun updateJarWarOffsetY(offset: Int) {
        _jarWarOffsetY.value = offset
    }

    fun updateSolveTestCreatePosition(position: Int) {
        _solveTestCreatePosition.value = position
    }

    fun updateColors(
        isDark: Boolean,
        appColors: ThemeColors,
    ) {
        _purpleColor.value = if (isDark) appColors.Purple6 else appColors.Purple3
        _redColor.value = if (isDark) appColors.Red6 else appColors.Red3
    }

    fun resetAnimationProgress() {
        _animationProgress.value = 0f
    }

    suspend fun animateHover(isHovered: Boolean) {
        if (isHovered) {
            val steps = 30
            val stepDuration = 16L * 3

            repeat(steps) { step ->
                _animationProgress.value = step / (steps - 1f)
                delay(stepDuration)
            }

            _animationProgress.value = 1f
        } else {
            _animationProgress.value = 0f
        }
    }
}
