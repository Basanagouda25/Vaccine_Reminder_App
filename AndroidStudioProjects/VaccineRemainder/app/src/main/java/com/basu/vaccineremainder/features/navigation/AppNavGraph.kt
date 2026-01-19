package com.basu.vaccineremainder.features.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.basu.vaccineremainder.data.database.AppDatabaseProvider
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.features.LearnScreen
import com.basu.vaccineremainder.features.auth.*
import com.basu.vaccineremainder.features.childprofile.*
import com.basu.vaccineremainder.features.dashboard.DashboardScreen
import com.basu.vaccineremainder.features.dashboardimport.UserViewModel
import com.basu.vaccineremainder.features.dashboardimport.UserViewModelFactory
import com.basu.vaccineremainder.features.faq.FAQScreen
import com.basu.vaccineremainder.features.notifications.NotificationScreen
import com.basu.vaccineremainder.features.profile.ParentProfileScreen
import com.basu.vaccineremainder.features.profile.ProviderProfileScreen
import com.basu.vaccineremainder.features.provider.*
import com.basu.vaccineremainder.features.schedule.*
import com.basu.vaccineremainder.util.RefreshManager
import com.basu.vaccineremainder.util.SessionManager
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {

    //val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    /* ---------- DATABASE + REPO ---------- */
    val db = AppDatabaseProvider.getDatabase(context)
    val repository = AppRepository(
        db.userDao(),
        db.childDao(),
        db.vaccineDao(),
        db.scheduleDao(),
        db.notificationDao(),
        db.providerDao()
    )

    val firebaseAuth = FirebaseAuth.getInstance()

    /* ---------- VIEWMODELS ---------- */
    val authViewModel: AuthViewModel = viewModel()

    val providerAuthViewModel: ProviderAuthViewModel = viewModel(
        factory = ProviderAuthViewModelFactory(repository, firebaseAuth)
    )

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(repository)
    )

    val addChildViewModel: AddChildViewModel = viewModel(
        factory = AddChildViewModelFactory(repository, firebaseAuth)
    )

    /* ---------- NAV HOST ---------- */
    NavHost(
        navController = navController,
        startDestination = "auth_gate"
    ) {

        /* ---------- AUTH GATE (SINGLE SOURCE OF TRUTH) ---------- */
        composable("auth_gate") {
            LaunchedEffect(Unit) {
                val user = firebaseAuth.currentUser
                if (user == null) {
                    navController.navigate(NavRoutes.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    // If already logged in, still force role selection
                    navController.navigate(NavRoutes.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        /* ---------- ROLE SELECTION ---------- */
        composable(NavRoutes.RoleSelection.route) {
            RoleSelectionScreen(
                onUserClick = { navController.navigate(NavRoutes.Login.route) },
                onProviderClick = { navController.navigate(NavRoutes.ProviderLogin.route) }
            )
        }

        /* ---------- PARENT LOGIN ---------- */
        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginResult = { user ->
                    if (user.uid.isNotBlank()) {

                        scope.launch {

                            SessionManager.login(
                                context,
                                SessionManager.ROLE_PARENT,
                                user.uid,
                                user.email ?: ""
                            )

                            SessionManager.saveParentName(context, user.name ?: "")
                            SessionManager.saveParentEmail(context, user.email ?: "")

                            // ✅ SUSPEND FUNCTIONS — NOW LEGAL
                            RefreshManager.triggerRefresh()

                            repository.syncChildrenForParentFromFirestore(
                                user.email ?: ""
                            )

                            navController.navigate(NavRoutes.Dashboard.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.Register.route)
                },
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

        /* ---------- PROVIDER LOGIN ---------- */
        composable(NavRoutes.ProviderLogin.route) {
            ProviderLoginScreen(
                viewModel = providerAuthViewModel,
                onLoginSuccess = { provider ->
                    SessionManager.login(
                        context,
                        SessionManager.ROLE_PROVIDER,
                        provider.providerId,
                        provider.email
                    )

                    providerAuthViewModel.loadProviderData()

                    navController.navigate(NavRoutes.ProviderDashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.ProviderRegister.route)
                },
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

        /* ---------- PARENT DASHBOARD ---------- */
        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                repository = repository,
                auth = firebaseAuth,
                onAddChildClick = { navController.navigate(NavRoutes.AddChild.route) },
                onChildListClick = { navController.navigate(NavRoutes.ChildList.route) },
                onVaccineScheduleClick = { navController.navigate(NavRoutes.VaccineList.route) },
                onNotificationClick = { navController.navigate(NavRoutes.Notifications.route) },
                onFaqClick = { navController.navigate("faq_parent") },
                onProfileClick = { navController.navigate(NavRoutes.Profile.route) },
                onLearnClick = { navController.navigate(NavRoutes.Learn.route) },
                onLogoutClick = {
                    firebaseAuth.signOut()
                    authViewModel.logout()
                    SessionManager.logout(context)
                    navController.navigate(NavRoutes.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        /* ---------- PROVIDER DASHBOARD ---------- */
        composable(NavRoutes.ProviderDashboard.route) {
            ProviderDashboardScreen(
                viewModel = providerAuthViewModel,
                onViewChildrenClick = {
                    navController.navigate(NavRoutes.ViewPatients.route)
                },
                onSendNotificationClick = {
                    navController.navigate(NavRoutes.ProviderSendNotification.route)
                },
                onVaccineCatalogClick = {
                    navController.navigate(NavRoutes.VaccineList.route)
                },
                onFaqClick = { navController.navigate("faq_provider") },
                onProviderProfileClick = {
                    navController.navigate(NavRoutes.ProviderProfile.route)
                },
                onLogoutClick = {
                    firebaseAuth.signOut()
                    SessionManager.logout(context)
                    navController.navigate(NavRoutes.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        /* ---------- COMMON SCREENS ---------- */
        composable(NavRoutes.Profile.route) {
            ParentProfileScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ProviderProfile.route) {
            ProviderProfileScreen(
                repository = repository,
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

        composable(NavRoutes.ChildList.route) {
            ChildListScreen(
                repository = repository,
                onChildSelected = { id ->
                    navController.navigate("${NavRoutes.ChildDetails.route}/$id")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("${NavRoutes.ChildDetails.route}/{childId}") {
            val childId = it.arguments?.getString("childId")?.toLong() ?: 0L
            ChildDetailsScreen(
                repository = repository,
                childId = childId,
                onBack = { navController.popBackStack() },
                onViewSchedule = {
                    navController.navigate("${NavRoutes.ChildSchedule.route}/$childId")
                }
            )
        }

        composable("${NavRoutes.ChildSchedule.route}/{childId}") {
            val childId = it.arguments?.getString("childId")?.toLong() ?: 0L
            ChildScheduleScreen(
                repository = repository,
                childId = childId,
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

        /* ---------- VACCINES ---------- */
        composable(NavRoutes.VaccineList.route) {
            VaccineListScreen(
                repository = repository,
                onVaccineSelected = { id ->
                    navController.navigate("${NavRoutes.VaccineDetails.route}/$id")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${NavRoutes.VaccineDetails.route}/{vaccineId}",
            arguments = listOf(navArgument("vaccineId") { type = NavType.IntType })
        ) {
            val id = it.arguments?.getInt("vaccineId") ?: 0
            VaccineDetailsScreen(
                repository = repository,
                vaccineId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable("faq_parent") {
            FAQScreen(role = "parent", onBack = { navController.popBackStack() })
        }

        composable("faq_provider") {
            FAQScreen(role = "provider", onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.Learn.route) {
            LearnScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
