package org.jikvict.browser.viewmodel

import androidx.lifecycle.ViewModel
import org.jikvict.browser.util.StateSaver

abstract class ExtendedViewModel(
    val savedStateHandle: StateSaver
) : ViewModel()