package com.mypec.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Dest(val route: String) {
    data object Home : Dest("home")
    data object History : Dest("history")
    data object Progress : Dest("progress")
    data object Tools : Dest("tools")
    data object Settings : Dest("settings")

    data object Program : Dest("program")
    data object Exercises : Dest("exercises")
    data object Records : Dest("records")
    data object Body : Dest("body")

    data object Workout : Dest("workout/{sessionId}") {
        fun create(sessionId: String) = "workout/$sessionId"
        const val ARG = "sessionId"
    }

    data object ExerciseDetail : Dest("exercise/{exerciseId}") {
        fun create(exerciseId: String) = "exercise/$exerciseId"
        const val ARG = "exerciseId"
    }
}

data class BottomItem(val dest: Dest, val label: String, val icon: ImageVector)

val bottomItems = listOf(
    BottomItem(Dest.Home, "Home", Icons.Filled.Home),
    BottomItem(Dest.History, "History", Icons.Filled.CalendarMonth),
    BottomItem(Dest.Progress, "Progress", Icons.Filled.BarChart),
    BottomItem(Dest.Tools, "Tools", Icons.Filled.Calculate),
    BottomItem(Dest.Settings, "Settings", Icons.Filled.Settings),
)
