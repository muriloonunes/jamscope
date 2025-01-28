package com.murile.nowplaying.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.util.DebugLogger
import com.murile.nowplaying.R
import com.murile.nowplaying.ui.components.APP_ROUTE
import com.murile.nowplaying.ui.components.LOGIN_ROUTE
import com.murile.nowplaying.ui.viewmodel.FriendsViewModel
import com.murile.nowplaying.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTela(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    friendsViewModel: FriendsViewModel,
) {
    val context = LocalContext.current
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    var imageError by remember { mutableStateOf(false) }
    val refreshing by profileViewModel.isRefreshing.collectAsStateWithLifecycle()
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .logger(DebugLogger())
            .build()
    }
    LaunchedEffect(Unit) {
        if (profileViewModel.shouldRefresh()) {
            profileViewModel.onRefresh()
        }
    }

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = {
            profileViewModel.onRefresh()
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            userProfile?.let {
                item {
                    AsyncImage(
                        model = it.imageUrl,
                        contentDescription = stringResource(
                            R.string.profile_pic_description,
                            it.username
                        ),
                        error = painterResource(R.drawable.profile_pic_placeholder) ,
                        placeholder = ColorPainter(color = MaterialTheme.colorScheme.surfaceContainerHigh),
                        imageLoader = imageLoader,
                        onError = { imageError = true },
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(
                        text = stringResource(R.string.hello_string, it.username),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                item {
                    Button(onClick = {
                        profileViewModel.logOutUser()
                        friendsViewModel.resetLastUpdateTimestamp()
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(APP_ROUTE) { inclusive = true }
                        }
                    }) {
                        Text(text = stringResource(R.string.logout))
                    }
                }
            }
        }
    }
}