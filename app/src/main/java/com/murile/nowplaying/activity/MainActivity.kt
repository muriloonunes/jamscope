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
import androidx.navigation.navigation
import com.murile.nowplaying.data.api.HttpClientProvider
import com.murile.nowplaying.data.session.DataStoreManager
import com.murile.nowplaying.ui.components.APP_ROUTE
import com.murile.nowplaying.ui.components.FRIENDS_SCREEN
import com.murile.nowplaying.ui.components.LOGIN_ROUTE
import com.murile.nowplaying.ui.components.LOGIN_SCREEN
import com.murile.nowplaying.ui.components.PROFILE_SCREEN
import com.murile.nowplaying.ui.components.SEARCH_SCREEN
import com.murile.nowplaying.ui.screen.HomePager
import com.murile.nowplaying.ui.screen.LoginScreen
import com.murile.nowplaying.ui.screen.ProfileTela
import com.murile.nowplaying.ui.screen.SearchTela
import com.murile.nowplaying.ui.theme.NowPlayingTheme
import com.murile.nowplaying.ui.viewmodel.FriendsViewModel
import com.murile.nowplaying.ui.viewmodel.LoginViewModel
import com.murile.nowplaying.ui.viewmodel.ProfileViewModel
import com.murile.nowplaying.ui.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var dataStoreManager: DataStoreManager

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

    override fun onDestroy() {
        super.onDestroy()
        HttpClientProvider.close()
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
                        dataStoreManager = dataStoreManager
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
                composable(SEARCH_SCREEN) {
                    SearchTela()
                }
                composable(PROFILE_SCREEN) {
                    ProfileTela(
                        navController = navController,
                        profileViewModel = profileViewModel,
                        friendsViewModel = friendsViewModel
                    )
                }
            }
        }
    }
}

