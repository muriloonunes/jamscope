package com.mno.jamscope.activity

import android.content.Intent
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.mno.jamscope.features.settings.viewmodel.SettingsViewModel
import com.mno.jamscope.ui.components.ChangelogDialog
import com.mno.jamscope.ui.navigator.NavigationAction
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.ui.navigator.RootHost
import com.mno.jamscope.ui.theme.LocalThemePreference
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.theme.JamscopeTheme
import com.mno.jamscope.ui.viewmodel.MainViewModel
import com.mno.jamscope.util.Stuff
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    private val mainViewModel by viewModels<MainViewModel>()

    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleWidgetIntent(intent)

        setContent {
//            val appOpenedTimes by splashViewModel.appOpenedTimes.collectAsState()
//            var showBottomSheet by remember { mutableStateOf(false) }
            val settingsUiState by settingsViewModel.uiState.collectAsState()
            val themePreference = settingsUiState.themePreference
            val startDestination by mainViewModel.startDestination.collectAsState()
            val navController = rememberNavController()
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            val showChangelog by mainViewModel.showChangelog.collectAsStateWithLifecycle()
            JamscopeTheme(themePreference = themePreference) {
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
                            mainViewModel.navActions.collect { action ->
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
                        if (showChangelog) {
                            ChangelogDialog {
                                mainViewModel.onDismissChangelog()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleWidgetIntent(intent)
    }

    private fun handleWidgetIntent(intent: Intent?) {
        val friendName = intent?.getStringExtra(Stuff.FROM_WIDGET)
        if (!friendName.isNullOrEmpty()) {
            mainViewModel.handleWidgetIntent(friendName)
        }
        intent?.removeExtra(Stuff.FROM_WIDGET)
    }

    private fun setupSplashScreen() {
        var keepSplashScreenOn = true
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.isLoading.collect {
                    keepSplashScreenOn = it
                }
            }
        }
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashScreenOn
        }
    }
}