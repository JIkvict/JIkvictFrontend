package org.jikvict.browser.icons

import androidx.compose.ui.graphics.vector.ImageVector
import org.jikvict.browser.icons.myiconpack.Code
import org.jikvict.browser.icons.myiconpack.Ijlogo
import org.jikvict.browser.icons.myiconpack.Menu
import org.jikvict.browser.icons.myiconpack.User
import kotlin.collections.List as ____KtList

public object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

public val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Code, Ijlogo, Menu, User)
    return __AllIcons!!
  }
