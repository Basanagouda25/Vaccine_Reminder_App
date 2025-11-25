package com.basu.vaccineremainder.features.navigation

sealed class NavRoutes(val route: String) {

    object Login : NavRoutes("login_screen")
    object Register : NavRoutes("register_screen")
    object Dashboard : NavRoutes("dashboard_screen")

    object AddChild : NavRoutes("add_child_screen")
    object ChildList : NavRoutes("child_list_screen")
    object ChildDetails : NavRoutes("child_details_screen")

    object VaccineList : NavRoutes("vaccine_list_screen")
    object VaccineDetails : NavRoutes("vaccine_details_screen")

    // ⭐ New route you must add
    object ChildSchedule : NavRoutes("child_schedule_screen/{childId}") {
        fun createRoute(childId: Int) = "child_schedule_screen/$childId"
    }
}
