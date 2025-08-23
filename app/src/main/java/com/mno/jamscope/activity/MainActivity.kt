package com.mno.jamscope.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.mno.jamscope.features.settings.viewmodel.SettingsViewModel
import com.mno.jamscope.ui.navigator.NavigationAction
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.ui.navigator.RootHost
import com.mno.jamscope.ui.theme.LocalThemePreference
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.theme.NowPlayingTheme
import com.mno.jamscope.ui.viewmodel.NavigationViewModel
import com.mno.jamscope.ui.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    private val splashViewModel by viewModels<SplashViewModel>()

    private val settingsViewModel by viewModels<SettingsViewModel>()

    //uhm...?
    private val navigationViewModel by viewModels<NavigationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
//            val appOpenedTimes by splashViewModel.appOpenedTimes.collectAsState()
//            var showBottomSheet by remember { mutableStateOf(false) }
            val settingsUiState by settingsViewModel.uiState.collectAsState()
            val themePreference = settingsUiState.themePreference
            val startDestination by splashViewModel.startDestination.collectAsState()
            val navController = rememberNavController()
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            NowPlayingTheme(themePreference = themePreference) {
                CompositionLocalProvider(
                    LocalThemePreference provides themePreference,
                    LocalWindowSizeClass provides windowSizeClass
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
//                        LaunchedEffect(appOpenedTimes) {
//                            if (appOpenedTimes == 10 && !showBottomSheet) {
//                                delay(2000)
//                                showBottomSheet = true
//                            }
//                        }
                        LaunchedEffect(Unit) {
                            navigationViewModel.navActions.collect { action ->
                                when (action) {
                                    is NavigationAction.Navigate -> {
                                        navController.navigate(
                                            route = action.destination,
                                            builder = action.navOptions
                                        )
                                    }

                                    is NavigationAction.Back -> {
                                        navController.navigateUp()
                                    }
                                }
                            }
                        }
                        startDestination?.let { RootHost(navController, it) }
//                        if (showBottomSheet) {
//                            RateAppBottomSheet(
//                                onDismissRequest = { showBottomSheet = false }
//                            )
//                        }
                    }
                }
            }
        }
    }

    private fun setupSplashScreen() {
        var keepSplashScreenOn = true
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                splashViewModel.isLoading.collect {
                    keepSplashScreenOn = it
                }
            }
        }
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashScreenOn
        }
    }
}