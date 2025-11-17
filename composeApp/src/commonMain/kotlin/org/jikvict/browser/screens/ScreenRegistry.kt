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
        CreateUserGroupScreenRegistrar,
        AssignmentsAdminScreenRegistrar,
        CreateAssignmentScreenRegistrar,
        EditAssignmentScreenRegistrar,
        EditAssignmentGroupScreenRegistrar,
        AssignmentInfoScreenRegistrar
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
        CreateUserGroupScreenRouterRegistrar,
        AssignmentsAdminScreenRouterRegistrar,
        CreateAssignmentScreenRouterRegistrar,
        EditAssignmentScreenRouterRegistrar,
        EditAssignmentGroupScreenRouterRegistrar,
        AssignmentInfoScreenRouterRegistrar
    )
