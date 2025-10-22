package com.mno.jamscope.features.settings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mno.jamscope.features.settings.ui.ActionButtons
import com.mno.jamscope.features.settings.ui.AppDescriptionCard
import com.mno.jamscope.features.settings.ui.AppInfoHeader
import com.mno.jamscope.features.settings.ui.AuthorCard

@Composable
fun AboutScreenVertical(
    modifier: Modifier = Modifier,
    versionName: String,
    onGithubProfileClick: () -> Unit,
    onMailClick: () -> Unit,
    onGithubProjectClick: () -> Unit,
    onBugReportClick: () -> Unit,
    onSeeLicenseClick: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AppInfoHeader(
                versionName = versionName,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(bottom = 8.dp)
            )
        }
        item {
            AppDescriptionCard()
        }
        item {
            AuthorCard(
                onGithubProfileClick = onGithubProfileClick,
                onMailClick = onMailClick
            )
        }
        item {
            ActionButtons(
                onGithubProjectClick = onGithubProjectClick,
                onBugReportClick = onBugReportClick,
                onSeeLicenseClick = onSeeLicenseClick,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}