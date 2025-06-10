package org.jikvict.browser.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jikvict.browser.constant.ThemeColors
import org.jikvict.browser.screens.FeedItem

class MakeJarScreenViewModel : ViewModel() {
    private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
    val feedItems = _feedItems.asStateFlow()

    private val _jarWarOffsetY = MutableStateFlow(0)
    val jarWarOffsetY = _jarWarOffsetY.asStateFlow()

    private val _jarWarHeightPx = MutableStateFlow(0)
    val jarWarHeightPx = _jarWarHeightPx.asStateFlow()

    private val _gridPosition = MutableStateFlow(0)
    val gridPosition = _gridPosition.asStateFlow()

    private val _animationProgress = MutableStateFlow(0f)
    val animationProgress = _animationProgress.asStateFlow()

    private val _purpleColor = MutableStateFlow<Color>(Color.Unspecified)
    val purpleColor = _purpleColor.asStateFlow()

    private val _redColor = MutableStateFlow<Color>(Color.Unspecified)
    val redColor = _redColor.asStateFlow()

    init {
        initializeFeedItems()
    }

    private fun initializeFeedItems() {
        val items = List(20) { index ->
            FeedItem("Title $index", "Description $index", height = 120 + (index % 3) * 40)
        }
        _feedItems.value = items
    }

    fun updateJarWarHeightPx(height: Int) {
        _jarWarHeightPx.value = height
    }

    fun updateJarWarOffsetY(offset: Int) {
        _jarWarOffsetY.value = offset
    }

    fun updateGridPosition(position: Int) {
        _gridPosition.value = position
    }

    fun updateColors(isDark: Boolean, appColors: ThemeColors) {
        _purpleColor.value = if (isDark) appColors.Purple6 else appColors.Purple3
        _redColor.value = if (isDark) appColors.Red6 else appColors.Red3
    }

    fun animateHover(isHovered: Boolean) {
        viewModelScope.launch {
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
}
