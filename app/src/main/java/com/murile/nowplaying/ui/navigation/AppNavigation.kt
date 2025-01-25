package com.murile.nowplaying.ui.navigation

import UserSessionManager

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.ui.screen.FriendsTela
import com.murile.nowplaying.ui.screen.LoginScreen
import com.murile.nowplaying.ui.viewmodel.LoginViewModel
import com.murile.nowplaying.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.runBlocking
import android.util.Log



