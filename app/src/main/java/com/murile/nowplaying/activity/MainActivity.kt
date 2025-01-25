package com.murile.nowplaying.activity

import UserSessionManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.ui.screen.FriendsTela
import com.murile.nowplaying.ui.screen.LoginScreen
import com.murile.nowplaying.ui.theme.NowPlayingTheme
import com.murile.nowplaying.ui.viewmodel.LoginViewModel
import com.murile.nowplaying.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val userSessionManager = UserSessionManager(applicationContext)
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            NowPlayingTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(userSessionManager)
                }
            }
        }
    }

    @Composable
    fun AppNavigation(userSessionManager: UserSessionManager) {
        val navController = rememberNavController()
        val isLoggedIn = runBlocking { userSessionManager.isUserLoggedIn() }
        if (isLoggedIn) {
            runBlocking { checkUser(userSessionManager) }
        }
        Log.i("AppNavigation", "isLoggedIn: $isLoggedIn")
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "login"
        ) {
            composable("home") {
                val profileViewModel: ProfileViewModel = viewModel()
                FriendsTela(
                    userSessionManager = userSessionManager,
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }
            composable("login") {
                val loginViewModel: LoginViewModel = viewModel()
                LoginScreen(
                    navController,
                    loginViewModel = loginViewModel
                )
            }
        }
    }

    private suspend fun checkUser(userSessionManager: UserSessionManager) {
        val userProfile = userSessionManager.getUserProfile()
        val links: Array<String> = ApiRequest.getUserInfo(userProfile!!.username)
        val newProfilePic = links[0]
        if (newProfilePic != userProfile.imageUrl) {
            userProfile.imageUrl = newProfilePic
            userSessionManager.saveUserProfile(userProfile)
        } else {
            return
        }
    }
}

