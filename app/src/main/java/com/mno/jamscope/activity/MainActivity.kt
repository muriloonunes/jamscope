package com.mno.jamscope.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.ui.components.WebViewLoader
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.NavigationAction
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.ui.screen.HomePager
import com.mno.jamscope.ui.screen.LoginScreen
import com.mno.jamscope.ui.screen.ProfileTela
import com.mno.jamscope.ui.screen.SettingsTela
import com.mno.jamscope.ui.theme.LocalThemePreference
import com.mno.jamscope.ui.theme.NowPlayingTheme
import com.mno.jamscope.ui.viewmodel.FriendsViewModel
import com.mno.jamscope.ui.viewmodel.SettingsViewModel
import com.mno.jamscope.ui.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var navigator: Navigator

    private val splashViewModel by viewModels<SplashViewModel>()

    private val friendsViewModel by viewModels<FriendsViewModel>()

    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSplashScreen()
        setContent {
            val themePreference by settingsViewModel.themePreference.collectAsState()
            val startDestination by splashViewModel.startDestination.collectAsState()
            val navController = rememberNavController()
            NowPlayingTheme(themePreference = themePreference) {
                CompositionLocalProvider(LocalThemePreference provides themePreference) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        LaunchedEffect(Unit) {
                            navigator.navigationActions.collect { action ->
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
                        startDestination?.let { AppNavigation(navController, it) }
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
            startDestination = startDestination
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
            }
        }
    }
}

