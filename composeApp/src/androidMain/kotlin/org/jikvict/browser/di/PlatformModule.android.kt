package org.jikvict.browser.di

import org.jikvict.browser.util.StateSaver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule =
    module {
        singleOf(::StateSaver)
    }
