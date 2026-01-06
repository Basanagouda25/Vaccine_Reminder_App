package com.basu.vaccineremainder.features.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.basu.vaccineremainder.data.database.AppDatabaseProvider
import com.basu.vaccineremainder.data.model.User
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.features.LearnScreen
import com.basu.vaccineremainder.features.auth.*
import com.basu.vaccineremainder.features.childprofile.AddChildScreen
import com.basu.vaccineremainder.features.childprofile.AddChildViewModel
import com.basu.vaccineremainder.features.childprofile.AddChildViewModelFactory
import com.basu.vaccineremainder.features.childprofile.ChildDetailsScreen
import com.basu.vaccineremainder.features.childprofile.ChildListScreen
import com.basu.vaccineremainder.features.dashboard.DashboardScreen
//import com.basu.vaccineremainder.features.dashboard.ProviderDashboardScreen
import com.basu.vaccineremainder.features.dashboardimport.UserViewModel
import com.basu.vaccineremainder.features.dashboardimport.UserViewModelFactory
import com.basu.vaccineremainder.features.faq.FAQScreen
import com.basu.vaccineremainder.features.notifications.NotificationScreen
import com.basu.vaccineremainder.features.profile.ParentProfileScreen
import com.basu.vaccineremainder.features.profile.ProviderProfileScreen
import com.basu.vaccineremainder.features.provider.*
import com.basu.vaccineremainder.features.schedule.ChildScheduleScreen
import com.basu.vaccineremainder.features.schedule.VaccineDetailsScreen
import com.basu.vaccineremainder.features.schedule.VaccineListScreen
import com.basu.vaccineremainder.util.RefreshManager
import com.basu.vaccineremainder.util.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = AppDatabaseProvider.getDatabase(context)
    val repository = AppRepository(
        db.userDao(),
        db.childDao(),
        db.vaccineDao(),
        db.scheduleDao(),
        db.notificationDao(),
        db.providerDao(),
    )

    val firebaseAuth = FirebaseAuth.getInstance()

    val authViewModel: AuthViewModel = viewModel()

    val providerAuthViewModel: ProviderAuthViewModel = viewModel(
        factory = ProviderAuthViewModelFactory(
            repository,
            FirebaseAuth.getInstance()
        )
    )


    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(repository)
    )

    val addChildViewModel: AddChildViewModel = viewModel(
        factory = AddChildViewModelFactory(
            repository,
            firebaseAuth
        )
    )



    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---------- ROLE SELECTION ----------
        composable(NavRoutes.RoleSelection.route) {
            RoleSelectionScreen(
                onUserClick = { navController.navigate(NavRoutes.Login.route) },
                onProviderClick = { navController.navigate(NavRoutes.ProviderLogin.route) }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginResult = { user: User ->   // ✅ FIXED
                    if (user.uid.isNotBlank()) {
                        scope.launch {
                            SessionManager.login(
                                context = context,
                                role = SessionManager.ROLE_PARENT,
                                userId = user.uid,
                                email = user.email ?: ""
                            )

                            SessionManager.saveParentName(context, user.name ?: "")
                            SessionManager.saveParentEmail(context, user.email ?: "")

                            RefreshManager.triggerRefresh()

                            repository.syncChildrenForParentFromFirestore(
                                user.email ?: ""
                            )

                            navController.navigate(NavRoutes.Dashboard.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate(NavRoutes.Register.route) },
                onBack = { navController.popBackStack() }
            )
        }









        composable(NavRoutes.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- PROVIDER AUTH ----------
        composable(NavRoutes.ProviderLogin.route) {
            ProviderLoginScreen(
                viewModel = providerAuthViewModel,
                onLoginSuccess = { provider ->
                    SessionManager.login(
                        context,
                        SessionManager.ROLE_PROVIDER,
                        provider.providerId,
                        email = provider.email
                    )

                    // Load Firestore children for provider (ProviderAuthViewModel handles this)
                    providerAuthViewModel.loadProviderData()

                    navController.navigate(NavRoutes.ProviderDashboard.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = { navController.navigate(NavRoutes.ProviderRegister.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ProviderRegister.route) {
            ProviderRegistrationScreen(
                viewModel = providerAuthViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                repository = repository,
                auth = firebaseAuth,
                onAddChildClick = { navController.navigate(NavRoutes.AddChild.route) },
                onChildListClick = { navController.navigate(NavRoutes.ChildList.route) },
                onVaccineScheduleClick = { navController.navigate(NavRoutes.VaccineList.route) },
                onNotificationClick = { navController.navigate(NavRoutes.Notifications.route) },
                onFaqClick = { navController.navigate("faq_parent") },
                onLogoutClick = {
                    SessionManager.logout(context)
                    navController.navigate(NavRoutes.RoleSelection.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(NavRoutes.Profile.route)
                },
                onLearnClick = {
                    navController.navigate(NavRoutes.Learn.route)
                }
            )
        }



        composable(NavRoutes.Profile.route) {
            ParentProfileScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }


        composable(NavRoutes.Learn.route) {
            LearnScreen(
                onBack = { navController.popBackStack() }
            )
        }


        composable(NavRoutes.AddChild.route) {

            AddChildScreen(
                viewModel = addChildViewModel,
                onChildAdded = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }





        // ---------- CHILD LIST (PARENT) ----------
        composable(NavRoutes.ChildList.route) {
            val parentEmail = SessionManager.getParentEmail(context)
            ChildListScreen(
                repository = repository,
                //parentEmail = parentEmail,
                onChildSelected = { childId ->
                    navController.navigate("${NavRoutes.ChildDetails.route}/$childId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- CHILD DETAILS ----------
        composable("${NavRoutes.ChildDetails.route}/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId")?.toLong() ?: 0L
            ChildDetailsScreen(
                repository = repository,
                childId = childId,
                onBack = { navController.popBackStack() },
                onViewSchedule = {
                    navController.navigate("${NavRoutes.ChildSchedule.route}/$childId")
                }
            )
        }

        // ---------- CHILD SCHEDULE ----------
        composable(
            route = "${NavRoutes.ChildSchedule.route}/{childId}",
            arguments = listOf(navArgument("childId") { type = NavType.StringType })
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId")?.toLong() ?: 0L

            ChildScheduleScreen(
                repository = repository,
                childId = childId,
                onBack = { navController.popBackStack() }
            )
        }

        // PROVIDER DASHBOARD
        composable(NavRoutes.ProviderDashboard.route) {
            ProviderDashboardScreen(
                viewModel = providerAuthViewModel,
                onViewChildrenClick = { navController.navigate(NavRoutes.ViewPatients.route) },
                onSendNotificationClick = { navController.navigate(NavRoutes.ProviderSendNotification.route) },
                // --- ADDED THIS LINE ---
                onVaccineCatalogClick = { navController.navigate(NavRoutes.VaccineList.route) },
                onLogoutClick = {
                    SessionManager.logout(context)
                    navController.navigate(NavRoutes.RoleSelection.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onFaqClick = { navController.navigate("faq_provider") },
                onProviderProfileClick = {
                    navController.navigate(NavRoutes.ProviderProfile.route)
                }
            )
        }


        composable(NavRoutes.ProviderProfile.route) {
            ProviderProfileScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }


        composable(NavRoutes.ViewPatients.route) {
            ViewPatientsScreen(
                viewModel = providerAuthViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ProviderSendNotification.route) {
            ProviderSendNotificationScreen(
                viewModel = providerAuthViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Notifications.route) {
            NotificationScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }



        // ---------- VACCINES ----------
        composable(NavRoutes.VaccineList.route) {
            VaccineListScreen(
                repository = repository,
                onVaccineSelected = { vaccineId ->
                    navController.navigate("${NavRoutes.VaccineDetails.route}/$vaccineId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${NavRoutes.VaccineDetails.route}/{vaccineId}",
            arguments = listOf(navArgument("vaccineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vaccineId = backStackEntry.arguments?.getInt("vaccineId") ?: 0

            VaccineDetailsScreen(
                repository = repository,
                vaccineId = vaccineId,
                onBack = { navController.popBackStack() } // <-- CORRECTED PARAMETER NAME
            )
        }

        composable("faq_parent") {
            FAQScreen(role = "parent", onBack = { navController.popBackStack() })
        }

        composable("faq_provider") {
            FAQScreen(role = "provider", onBack = { navController.popBackStack() })
        }

    }
}
