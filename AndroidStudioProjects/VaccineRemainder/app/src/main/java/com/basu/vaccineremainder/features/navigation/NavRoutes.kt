package com.basu.vaccineremainder.features.navigation

sealed class NavRoutes(val route: String) {

    object RoleSelection : NavRoutes("role_selection")
    object Login : NavRoutes("login_screen")
    object Register : NavRoutes("register_screen")
    object Dashboard : NavRoutes("dashboard_screen")

    object AddChild : NavRoutes("add_child_screen")
    object ChildList : NavRoutes("child_list_screen")
    object ChildDetails : NavRoutes("child_details_screen/{childId}") {
        fun createRoute(childId: Int) = "child_details_screen/$childId"
    }

    object VaccineList : NavRoutes("vaccine_list_screen")
    object VaccineDetails : NavRoutes("vaccine_details_screen")

    object ChildSchedule : NavRoutes("child_schedule_screen/{childId}") {
        fun createRoute(childId: Int) = "child_schedule_screen/$childId"
    }

    object Notifications : NavRoutes("notifications_screen")

    object ProviderLogin : NavRoutes("provider_login_screen")
    object ProviderRegister : NavRoutes("provider_register_screen")
    object ProviderDashboard : NavRoutes("provider_dashboard_screen")

    object ProviderSendNotification : NavRoutes("provider_send_notification")


    object ViewPatients : NavRoutes("view_patients")
}
