package org.jikvict.browser.icons

import androidx.compose.ui.graphics.vector.ImageVector
import org.jikvict.browser.icons.myiconpack.Lockeddark
import org.jikvict.browser.icons.myiconpack.Lockedlight
import org.jikvict.browser.icons.myiconpack.Taskstatusdark
import org.jikvict.browser.icons.myiconpack.Taskstatusdonedark
import org.jikvict.browser.icons.myiconpack.Taskstatusdonelight
import org.jikvict.browser.icons.myiconpack.Taskstatusfaileddark
import org.jikvict.browser.icons.myiconpack.Taskstatusfailedlight
import org.jikvict.browser.icons.myiconpack.Taskstatuslight
import kotlin.collections.List as ____KtList

public object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

public val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Lockeddark, Lockedlight, Taskstatusdark, Taskstatusdonedark,
        Taskstatusdonelight, Taskstatusfaileddark, Taskstatusfailedlight, Taskstatuslight)
    return __AllIcons!!
  }
