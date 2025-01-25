package com.murile.nowplaying.ui.screen

import UserSessionManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.util.DebugLogger
import com.murile.nowplaying.R
import com.murile.nowplaying.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun FriendsTela(
    userSessionManager: UserSessionManager,
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val userProfile by profileViewModel.userProfile.collectAsState()
    LaunchedEffect(Unit) {
        profileViewModel.getProfile(context)
    }

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .logger(DebugLogger())
            .build()
    }
    Scaffold(topBar = {}, content = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            userProfile?.let {
                AsyncImage(
                    model = it.imageUrl,
                    contentDescription = stringResource(
                        R.string.profile_pic_description,
                        it.username
                    ),
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.hello_string, it.username),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = {
                    runBlocking {
                        userSessionManager.clearUserSession()
                        navController.navigate("login") {
                            popUpTo("friends") { inclusive = true }
                        }
                    }
                }) {
                    Text(text = stringResource(R.string.logout))
                }
            }
        }
    }, bottomBar = {})
}