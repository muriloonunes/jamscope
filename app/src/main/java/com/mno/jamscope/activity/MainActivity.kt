package com.mno.jamscope.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.data.session.UserDataStoreManager
import com.mno.jamscope.ui.components.APP_ROUTE
import com.mno.jamscope.ui.components.FRIENDS_SCREEN
import com.mno.jamscope.ui.components.LOGIN_ROUTE
import com.mno.jamscope.ui.components.LOGIN_SCREEN
import com.mno.jamscope.ui.components.PROFILE_SCREEN
import com.mno.jamscope.ui.components.SETTINGS_SCREEN
import com.mno.jamscope.ui.screen.HomePager
import com.mno.jamscope.ui.screen.LoginScreen
import com.mno.jamscope.ui.screen.ProfileTela
import com.mno.jamscope.ui.screen.SettingsTela
import com.mno.jamscope.ui.theme.NowPlayingTheme
import com.mno.jamscope.ui.viewmodel.FriendsViewModel
import com.mno.jamscope.ui.viewmodel.LoginViewModel
import com.mno.jamscope.ui.viewmodel.ProfileViewModel
import com.mno.jamscope.ui.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userDataStoreManager: UserDataStoreManager

    @Inject
    lateinit var userRepository: UserRepository

    private val splashViewModel by viewModels<SplashViewModel>()

    private val friendsViewModel by viewModels<FriendsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSplashScreen()
        setContent {
            NowPlayingTheme {
                val startDestination by splashViewModel.startDestination.collectAsState()
                Surface(color = MaterialTheme.colorScheme.background) {
                    startDestination?.let { AppNavigation(it) }
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
        startDestination: String
    ) {
        val navController = rememberNavController()
        val profileViewModel by viewModels<ProfileViewModel>()
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            navigation(
                startDestination = LOGIN_SCREEN,
                route = LOGIN_ROUTE
            ) {
                composable(LOGIN_SCREEN) {
                    val loginViewModel by viewModels<LoginViewModel>()
                    LoginScreen(
                        navController,
                        loginViewModel = loginViewModel,
                        userRepository = userRepository
                    )
                }
            }
            navigation(
                startDestination = FRIENDS_SCREEN,
                route = APP_ROUTE
            ) {
                composable(FRIENDS_SCREEN) {
                    HomePager(
                        navController,
                        profileViewModel,
                        friendsViewModel
                    )
                }
                composable(SETTINGS_SCREEN) {
                    SettingsTela()
                }
                composable(PROFILE_SCREEN) {
                    ProfileTela(
                        navController = navController,
                        profileViewModel = profileViewModel,
                        listState = LazyListState()
                    )
                }
            }
        }
    }
}

