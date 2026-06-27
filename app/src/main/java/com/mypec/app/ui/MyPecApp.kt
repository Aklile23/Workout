package com.mypec.app.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mypec.app.ui.components.AppBackground
import com.mypec.app.ui.components.FloatingNavBar
import com.mypec.app.ui.components.NavItem
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.mypec.app.ui.navigation.Dest
import com.mypec.app.ui.navigation.bottomItems
import com.mypec.app.feature.body.BodyScreen
import com.mypec.app.feature.exercises.ExerciseDetailScreen
import com.mypec.app.feature.exercises.ExercisesScreen
import com.mypec.app.feature.history.HistoryScreen
import com.mypec.app.feature.home.HomeScreen
import com.mypec.app.feature.profile.SettingsScreen
import com.mypec.app.feature.program.ProgramScreen
import com.mypec.app.feature.progress.ProgressScreen
import com.mypec.app.feature.records.RecordsScreen
import com.mypec.app.feature.tools.ToolsScreen
import com.mypec.app.feature.workout.WorkoutScreen

@Composable
fun MyPecApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomItems.any { it.dest.route == currentRoute }

    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    val current = backStackEntry?.destination
                    val selectedRoute = bottomItems.firstOrNull { item ->
                        current?.hierarchy?.any { it.route == item.dest.route } == true
                    }?.dest?.route
                    FloatingNavBar(
                        items = bottomItems.map { NavItem(it.dest.route, it.label, it.icon) },
                        selectedRoute = selectedRoute,
                        onSelect = { item ->
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Dest.Home.route,
                modifier = Modifier.padding(padding),
                enterTransition = { fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 12 } },
                exitTransition = { fadeOut(tween(220)) + slideOutHorizontally(tween(220)) { -it / 12 } },
            ) {
            composable(Dest.Home.route) { HomeScreen(navController) }
            composable(Dest.History.route) { HistoryScreen(navController) }
            composable(Dest.Progress.route) { ProgressScreen(navController) }
            composable(Dest.Tools.route) { ToolsScreen() }
            composable(Dest.Settings.route) { SettingsScreen() }

            composable(Dest.Program.route) { ProgramScreen(navController) }
            composable(Dest.Exercises.route) { ExercisesScreen(navController) }
            composable(Dest.Records.route) { RecordsScreen(navController) }
            composable(Dest.Body.route) { BodyScreen(navController) }

            composable(
                route = Dest.Workout.route,
                arguments = listOf(navArgument(Dest.Workout.ARG) { type = NavType.StringType }),
            ) { WorkoutScreen(navController) }

            composable(
                route = Dest.ExerciseDetail.route,
                arguments = listOf(navArgument(Dest.ExerciseDetail.ARG) { type = NavType.StringType }),
            ) { ExerciseDetailScreen(navController) }
            }
        }
    }
}
