package com.mypec.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mypec.app.data.settings.ThemeMode
import com.mypec.app.ui.AppViewModel
import com.mypec.app.ui.MyPecApp
import com.mypec.app.ui.SplashScreen
import com.mypec.app.ui.theme.MyPecTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by appViewModel.settings.collectAsStateWithLifecycle()
            val dark = when (settings.themeMode) {
                ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            MyPecTheme(darkTheme = dark, dynamicColor = false) {
                var showSplash by remember { mutableStateOf(true) }
                Crossfade(targetState = showSplash, animationSpec = tween(500), label = "splash") { splash ->
                    if (splash) {
                        SplashScreen(onFinished = { showSplash = false })
                    } else {
                        MyPecApp()
                    }
                }
            }
        }
    }
}
