package com.mno.jamscope.features.settings.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreenHorizontal(
    modifier: Modifier = Modifier,
    versionName: String,
    onGithubProfileClick: () -> Unit,
    onMailClick: () -> Unit,
    onGithubProjectClick: () -> Unit,
    onBugReportClick: () -> Unit,
    onSeeLicenseClick: () -> Unit,
    onShowChangelogClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxSize()
    ) {
        AppInfoHeader(
            versionName = versionName,
            verticalArrangement = Arrangement.Center,
            onShowChangelogClick = onShowChangelogClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
                )
                .padding(horizontal = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
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
                    onSeeLicenseClick = onSeeLicenseClick
                )
            }
        }
    }
}