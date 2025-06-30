package com.mno.jamscope.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.mno.jamscope.ui.components.LoadLibrariesScreen
import com.mno.jamscope.ui.components.WebViewLoader
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.NavigationAction
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.ui.screen.HomePager
import com.mno.jamscope.ui.screen.LoginScreen
import com.mno.jamscope.ui.screen.ProfileTela
import com.mno.jamscope.ui.screen.SettingsTela
import com.mno.jamscope.ui.theme.LocalThemePreference
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.theme.NowPlayingTheme
import com.mno.jamscope.ui.viewmodel.FriendsViewModel
import com.mno.jamscope.ui.viewmodel.NavigationViewModel
import com.mno.jamscope.ui.viewmodel.SettingsViewModel
import com.mno.jamscope.ui.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    private val splashViewModel by viewModels<SplashViewModel>()

    private val friendsViewModel by viewModels<FriendsViewModel>()

    private val settingsViewModel by viewModels<SettingsViewModel>()

    //uhm...?
    private val navigationViewModel by viewModels<NavigationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val themePreference by settingsViewModel.themePreference.collectAsState()
            val startDestination by splashViewModel.startDestination.collectAsState()
//            val appOpenedTimes by splashViewModel.appOpenedTimes.collectAsState()
            var showBottomSheet by remember { mutableStateOf(false) }
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
                            //nao sei se isso funciona mas resolve o memory leak que a versao comentada tinha criado
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
//                            navigator.navigationActions.collect { action ->
//                                when (action) {
//                                    is NavigationAction.Navigate -> {
//                                        navController.navigate(
//                                            route = action.destination,
//                                            builder = action.navOptions
//                                        )
//                                    }
//
//                                    is NavigationAction.Back -> {
//                                        navController.navigateUp()
//                                    }
//                                }
//                            }
                        }
                        startDestination?.let { AppNavigation(navController, it) }
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

    @Composable
    fun AppNavigation(
        navController: NavHostController,
        startDestination: Destination
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
                )
            },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
                )
            }
        ) {
            navigation<Destination.LoginRoute>(
                startDestination = Destination.LoginScreen,
            ) {
                composable<Destination.LoginScreen> {
                    LoginScreen()
                }
            }
            navigation<Destination.AppRoute>(
                startDestination = Destination.FriendsScreen
            ) {
                composable<Destination.FriendsScreen> {
                    HomePager(
                        friendsViewModel
                    )
                }
                composable<Destination.SettingsScreen> {
                    SettingsTela(
                        settingsViewModel = settingsViewModel
                    )
                }
                composable<Destination.ProfileScreen> {
                    ProfileTela(
                        listState = rememberLazyListState()
                    )
                }
                composable<Destination.WebViewScreen> {
                    WebViewLoader()
                }
                composable<Destination.LibrariesScreen> {
                    LoadLibrariesScreen()
                }
            }
        }
    }
}