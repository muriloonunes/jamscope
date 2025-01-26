package com.murile.nowplaying.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.data.session.UserSessionManager
import com.murile.nowplaying.ui.components.FRIENDS_ROUTE
import com.murile.nowplaying.ui.components.LOGIN_ROUTE
import com.murile.nowplaying.ui.screen.LoginScreen
import com.murile.nowplaying.ui.screen.ProfileTela
import com.murile.nowplaying.ui.theme.NowPlayingTheme
import com.murile.nowplaying.ui.viewmodel.LoginViewModel
import com.murile.nowplaying.ui.viewmodel.ProfileViewModel
import com.murile.nowplaying.ui.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userSessionManager: UserSessionManager

    @Inject
    lateinit var apiRequest: ApiRequest

    private val splashViewModel by viewModels<SplashViewModel>()

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

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(FRIENDS_ROUTE) {
                val profileViewModel by viewModels<ProfileViewModel>()
                ProfileTela(
                    userSessionManager = userSessionManager,
                    navController,
                    profileViewModel = profileViewModel
                )
            }
            composable(LOGIN_ROUTE) {
                val loginViewModel by viewModels<LoginViewModel>()
                LoginScreen(
                    navController,
                    loginViewModel = loginViewModel,
                    userSessionManager = userSessionManager
                )
            }
        }
    }
}

