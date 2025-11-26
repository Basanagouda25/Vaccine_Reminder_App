package com.basu.vaccineremainder.features.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basu.vaccineremainder.data.database.AppDatabaseProvider
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.features.auth.AuthViewModel
import com.basu.vaccineremainder.features.auth.AuthViewModelFactory
import com.basu.vaccineremainder.features.auth.LoginScreen
import com.basu.vaccineremainder.features.auth.RegisterScreen
import com.basu.vaccineremainder.features.childprofile.AddChildScreen
import com.basu.vaccineremainder.features.childprofile.ChildDetailsScreen
import com.basu.vaccineremainder.features.childprofile.ChildListScreen
import com.basu.vaccineremainder.features.dashboard.DashboardScreen
import com.basu.vaccineremainder.features.schedule.ChildScheduleScreen
import com.basu.vaccineremainder.features.schedule.VaccineDetailsScreen
import com.basu.vaccineremainder.features.schedule.VaccineListScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {

    val context = LocalContext.current

    // DB + Repository + ViewModel creation inside NavGraph
    val db = AppDatabaseProvider.getDatabase(context)
    val repository = AppRepository(
        db.userDao(),
        db.childDao(),
        db.vaccineDao(),
        db.scheduleDao()
    )
    val viewModel = ViewModelProvider(
        context as androidx.activity.ComponentActivity,
        AuthViewModelFactory(repository)
    )[AuthViewModel::class.java]

    NavHost(
        navController = navController,
        startDestination = startDestination

    ) {

        //Add child
        composable(NavRoutes.AddChild.route) {
            AddChildScreen(
                repository = repository,
                parentId = 1,   // TEMPORARY (replace later with logged-in userId)
                onChildAdded = { navController.popBackStack() }
            )
        }

        ///childlist screen
        composable(NavRoutes.ChildList.route) {
            ChildListScreen(
                repository = repository,
                parentId = 1,
                onChildSelected = { childId ->
                    navController.navigate("${NavRoutes.ChildDetails.route}/$childId")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        //child Detail screen
        composable("${NavRoutes.ChildDetails.route}/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId")?.toInt() ?: 0

            ChildDetailsScreen(
                repository = repository,
                childId = childId,
                onBack = { navController.popBackStack() },
                onViewSchedule = { navController.navigate(NavRoutes.ChildSchedule.createRoute(childId)) }
            )
        }




        composable(NavRoutes.ChildSchedule.route) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId")?.toInt() ?: -1

            ChildScheduleScreen(
                repository = repository,
                childId = childId,
                onBack = { navController.popBackStack() }
            )
        }



        //vaccine list screen
        composable(NavRoutes.VaccineList.route) {
            VaccineListScreen(
                repository = repository,
                onVaccineSelected = { vaccineId ->
                    navController.navigate("${NavRoutes.VaccineDetails.route}/$vaccineId")
                }
            )
        }

        //vaccine details
        composable("${NavRoutes.VaccineDetails.route}/{vaccineId}") { backStackEntry ->
            val vaccineId = backStackEntry.arguments?.getString("vaccineId")?.toInt() ?: 0

            VaccineDetailsScreen(
                repository = repository,
                vaccineId = vaccineId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // ------- LOGIN SCREEN -------
        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginResult = { success ->
                    if (success) {
                        navController.navigate(NavRoutes.Dashboard.route) {
                            popUpTo(NavRoutes.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.Register.route)
                }
            )
        }

        // ------- REGISTER SCREEN -------
        composable(NavRoutes.Register.route) {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterResult = { success ->
                    if (success) navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ------- DASHBOARD PLACEHOLDER -------
        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                onAddChildClick = {
                    navController.navigate(NavRoutes.AddChild.route)
                },
                onChildListClick = {
                    navController.navigate(NavRoutes.ChildList.route)
                },
                onVaccineScheduleClick = {
                    navController.navigate(NavRoutes.VaccineList.route)
                }
            )
        }

    }
}
