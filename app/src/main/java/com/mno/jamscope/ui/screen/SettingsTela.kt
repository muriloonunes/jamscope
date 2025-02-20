package com.mno.jamscope.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.mno.jamscope.R
import com.mno.jamscope.ui.viewmodel.SettingsViewModel
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun SettingsTela(
    settingsViewModel: SettingsViewModel
) {
    val profileUpdated by settingsViewModel.isProfileUpdated.collectAsStateWithLifecycle()
    val userProfile by settingsViewModel.userProfile.collectAsStateWithLifecycle()

    LazyColumnScrollbar(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        state = rememberLazyListState(),
        settings = ScrollbarSettings(
            thumbUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
            thumbSelectedColor = MaterialTheme.colorScheme.onSurface,
            scrollbarPadding = 2.dp
        ),
    ) {
        LazyColumn {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = userProfile?.imageUrl,
                        contentDescription = stringResource(
                            R.string.profile_pic_description,
                            userProfile?.username ?: "User"
                        ),
                        error = painterResource(R.drawable.profile_pic_placeholder),
                        placeholder = painterResource(R.drawable.baseline_account_circle_24),
                        modifier = Modifier
                            .size(70.dp)
                            .padding(8.dp)
                            .clip(CircleShape)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = userProfile?.username ?: stringResource(R.string.user),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                            )
                        )
                    }
                }
            }
        }
    }
}