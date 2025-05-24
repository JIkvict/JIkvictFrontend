package org.jikvict.browser.screens

val registeredScreens = listOf<ScreenRegistrar<out NavigableScreen>>(
    HomeScreenRegistrar,
    NotFoundScreenRegistrar
)
val routers = listOf<ScreenRouterRegistrar<out NavigableScreen>>(
    HomeScreenRouterRegistrar,
    NotFoundScreenRouterRegistrar
)