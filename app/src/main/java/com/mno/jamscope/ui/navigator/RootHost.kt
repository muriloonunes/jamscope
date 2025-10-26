package com.mno.jamscope.ui.navigator

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.mno.jamscope.R
import com.mno.jamscope.features.login.nativeauth.ui.LoginTelaNative
import com.mno.jamscope.features.login.viewmodel.NativeLoginViewModel
import com.mno.jamscope.features.login.webauth.ui.LoginTelaWeb
import com.mno.jamscope.features.login.webauth.ui.WebLoginWrapper
import com.mno.jamscope.features.login.webauth.viewmodel.WebLoginViewModel
import com.mno.jamscope.features.settings.ui.about.AboutScreen
import com.mno.jamscope.features.settings.ui.libraries.LoadLibrariesLicenseScreen
import com.mno.jamscope.features.settings.viewmodel.SettingsViewModel
import com.mno.jamscope.features.webview.ui.WebViewLoader
import com.mno.jamscope.ui.screen.JamHomeRail
import com.mno.jamscope.ui.screen.JamHomeScaffold
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.util.ProfileScreenCaller
import com.mno.jamscope.ui.util.SettingsScreenCaller
import com.mno.jamscope.util.Stuff
import io.ktor.http.URLBuilder
import io.ktor.http.set

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
            startDestination = Destination.LoginScreenWeb,
        ) {
            composable<Destination.LoginScreenNative> {
                val loginViewModel: NativeLoginViewModel = hiltViewModel()
                val state by loginViewModel.state.collectAsStateWithLifecycle()
                LoginTelaNative(
                    state = state,
                    onLoginClick = { loginViewModel.login() },
                    onPasswordChange = { loginViewModel.onPasswordChange(it) },
                    onUsernameChange = { loginViewModel.onUsernameChange(it) },
                    openCreateAccount = { loginViewModel.openCreateAccount(it) },
                    onPasswordVisibilityChange = { loginViewModel.onPasswordVisibilityChange() }
                )
            }
            composable<Destination.LoginScreenWeb> { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(Destination.LoginRoute)
                }
                val webViewModel: WebLoginViewModel = hiltViewModel(parentEntry)
                LoginTelaWeb(
                    onLoginClick = {
                        webViewModel.navigateToWebView()
                    }
                )
            }
            composable<Destination.LastFmWebLoginScreen> { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(Destination.LoginRoute)
                }
                val webViewModel: WebLoginViewModel = hiltViewModel(parentEntry)
                val urlBuilder = URLBuilder(Stuff.AUTH_URL)
                urlBuilder.set {
                    parameters.append("api_key", Stuff.LAST_KEY)
                    parameters.append("cb", "${Stuff.DEEPLINK_PROTOCOL_NAME}://auth")
                }
                WebLoginWrapper(
                    url = urlBuilder.buildString(),
                    onNavigateBack = { webViewModel.navigateBack() },
                    onCallbackDetected = { webViewModel.handleCallbackUrl(it) }
                )
            }
        }
        navigation<Destination.AppRoute>(
            startDestination = Destination.FriendsScreen
        ) {
            composable<Destination.FriendsScreen> {
                val windowWidthSizeClass = LocalWindowSizeClass.current.widthSizeClass
                when (windowWidthSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        JamHomeScaffold()
                    }

                    else -> {
                        JamHomeRail()
                    }
                }
            }
            composable<Destination.SettingsScreen> {
                SettingsScreenCaller(showTopAppBar = true)
            }
            composable<Destination.ProfileScreen> {
                ProfileScreenCaller()
            }
            composable<Destination.SuggestFeatureScreen> {
                val webViewModel: WebLoginViewModel = hiltViewModel()
                WebViewLoader(
                    onNavigateBack = { webViewModel.navigateBack() },
                    title = stringResource(R.string.suggest_feature_setting),
                    url = "https://forms.gle/zwhTjknzo6NVKh9MA"
                )
            }
            composable<Destination.LibrariesLicenseScreen> { backStackEntry ->
                val screenType: Destination.LibrariesLicenseScreen = backStackEntry.toRoute()
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                LoadLibrariesLicenseScreen(
                    screenType = screenType.screenType,
                    onNavigateBack = { settingsViewModel.navigateBack() }
                )
            }
            composable<Destination.AboutScreen> {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                val state by settingsViewModel.uiState.collectAsState()
                AboutScreen(
                    uiState = state,
                    onNavigateBack = { settingsViewModel.navigateBack() },
                    onBugReportClick = { settingsViewModel.sendBugReportMail(it) },
                    onGithubProfileClick = { settingsViewModel.openGithubProfile(it) },
                    onMailClick = { settingsViewModel.sendMailToDeveloper(it) },
                    onGithubProjectClick = { settingsViewModel.openGithubProject(it) },
                    onSeeLicenseClick = {
                        settingsViewModel.navigateToLibrariesLicenseScreen(
                            ScreenType.LICENSE
                        )
                    },
                    onShowChangelogClick = {
                        settingsViewModel.showChangelogDialog()
                    },
                    onHideChangelogDialog = {
                        settingsViewModel.hideChangelogDialog()
                    },
                )
            }
        }
    }
}