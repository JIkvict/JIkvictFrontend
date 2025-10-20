package org.jikvict.browser.screens

val registeredScreens =
    listOf<ScreenRegistrar<out NavigableScreen>>(
        NotFoundScreenRegistrar,
        MakeJarScreenRegistrar,
        TasksScreenRegistrar,
        LoginScreenRegistrar,
        AdminScreenRegistrar,
        ProfileScreenRegistrar,
        UserGroupScreenRegistrar,
    )
val routers =
    listOf<ScreenRouterRegistrar<out NavigableScreen>>(
        NotFoundScreenRouterRegistrar,
        MakeJarScreenRouterRegistrar,
        TasksScreenRouterRegistrar,
        LoginScreenRouterRegistrar,
        AdminScreenRouterRegistrar,
        ProfileScreenRouterRegistrar,
        UserGroupScreenRouterRegistrar,
    )
