package org.jikvict.browser.icons

import androidx.compose.ui.graphics.vector.ImageVector
import org.jikvict.browser.icons.myiconpack.Unlockeddark
import org.jikvict.browser.icons.myiconpack.Unlockedlight
import kotlin.collections.List as ____KtList

object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Unlockeddark, Unlockedlight)
    return __AllIcons!!
  }
