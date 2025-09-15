package com.mno.jamscope.ui.navigator

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.features.login.ui.LoginScreen
import com.mno.jamscope.features.login.viewmodel.LoginViewModel
import com.mno.jamscope.features.settings.viewmodel.SettingsViewModel
import com.mno.jamscope.features.settings.ui.LoadLibrariesScreen
import com.mno.jamscope.features.settings.ui.WebViewLoader
import com.mno.jamscope.ui.screen.JamHomeScaffold
import com.mno.jamscope.ui.screen.JamHomeRail
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.util.ProfileScreenCaller
import com.mno.jamscope.ui.util.SettingsScreenCaller

@Composable
fun RootHost(
    navController: NavHostController,
    startDestination: Destination,
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
                val loginViewModel: LoginViewModel = hiltViewModel()
                val state by loginViewModel.state.collectAsStateWithLifecycle()
                LoginScreen(
                    state = state,
                    onLoginClick = { loginViewModel.login() },
                    onPasswordChange = { loginViewModel.onPasswordChange(it) },
                    onUsernameChange = { loginViewModel.onUsernameChange(it) },
                    openCreateAccount = { loginViewModel.openCreateAccount(it) },
                    onPasswordVisibilityChange = { loginViewModel.onPasswordVisibilityChange() }
                )
            }
        }
        navigation<Destination.AppRoute>(
            startDestination = Destination.FriendsScreen
        ) {
            composable<Destination.FriendsScreen> {
                val windowWidthSizeClass = LocalWindowSizeClass.current.windowWidthSizeClass
                when (windowWidthSizeClass) {
                    WindowWidthSizeClass.COMPACT -> {
                        JamHomeScaffold()
                    }

                    else -> {
                        JamHomeRail()
                    }
                }
            }
            composable<Destination.SettingsScreen> {
                SettingsScreenCaller()
            }
            composable<Destination.ProfileScreen> {
                ProfileScreenCaller()
            }
            composable<Destination.WebViewScreen> {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                WebViewLoader(
                    onNavigateBack = { settingsViewModel.navigateBack() }
                )
            }
            composable<Destination.LibrariesScreen> {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                LoadLibrariesScreen(
                    onNavigateBack = { settingsViewModel.navigateBack() }
                )
            }
        }
    }
}