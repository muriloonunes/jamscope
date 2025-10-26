package com.mno.jamscope.features.settings.ui.about

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.features.settings.state.SettingsUiState
import com.mno.jamscope.ui.components.ChangelogDialog
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(
    uiState: SettingsUiState,
    onNavigateBack: () -> Unit,
    onGithubProfileClick: (Context) -> Unit,
    onMailClick: (Context) -> Unit,
    onGithubProjectClick: (Context) -> Unit,
    onBugReportClick: (Context) -> Unit,
    onSeeLicenseClick: () -> Unit,
    onShowChangelogClick: () -> Unit,
    onHideChangelogDialog: () -> Unit,
) {
    val context = LocalContext.current
    val windowSizeClass = LocalWindowSizeClass.current
    val windowWidth = windowSizeClass.widthSizeClass
    val showChangelogDialog = uiState.showChangelogDialog
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.about_setting_tile),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            )
        }) { innerPadding ->
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName =
            packageInfo?.versionName ?: stringResource(id = R.string.unknown)
        when (windowWidth) {
            WindowWidthSizeClass.Compact -> {
                AboutScreenVertical(
                    modifier = Modifier.padding(innerPadding),
                    versionName = versionName,
                    onGithubProfileClick = { onGithubProfileClick(context) },
                    onMailClick = { onMailClick(context) },
                    onGithubProjectClick = { onGithubProfileClick(context) },
                    onBugReportClick = { onBugReportClick(context) },
                    onSeeLicenseClick = { onSeeLicenseClick() },
                    onShowChangelogClick = { onShowChangelogClick() },
                )
            }

            WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
                AboutScreenHorizontal(
                    modifier = Modifier.padding(innerPadding),
                    versionName = versionName,
                    onGithubProfileClick = { onGithubProfileClick(context) },
                    onMailClick = { onMailClick(context) },
                    onGithubProjectClick = { onGithubProjectClick(context) },
                    onBugReportClick = { onBugReportClick(context) },
                    onSeeLicenseClick = onSeeLicenseClick,
                    onShowChangelogClick = { onShowChangelogClick() },
                )
            }
        }
    }
    if (showChangelogDialog) {
        ChangelogDialog {
            onHideChangelogDialog()
        }
    }
}

@Composable
private fun AboutCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            content()
        }
    }
}

@Composable
fun AppInfoHeader(
    versionName: String,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    onShowChangelogClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = verticalArrangement
    ) {
        Icon(
            painter = painterResource(id = R.drawable.app_icon_filled_black),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(172.dp)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    onShowChangelogClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.version, versionName),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .padding(start = 8.dp, end = 4.dp)
            )
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                contentDescription = stringResource(id = R.string.show_changelog),
                modifier = Modifier
                    .size(13.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
fun AppDescriptionCard() {
    AboutCard {
        Text(
            text = stringResource(R.string.thanks_for_using),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.about_app),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun AuthorCard(
    onGithubProfileClick: () -> Unit,
    onMailClick: () -> Unit,
) {
    AboutCard {
        Text(
            text = stringResource(R.string.made_by),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Murilo Nunes",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onGithubProfileClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.github),
                    contentDescription = "GitHub",
                    tint = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = onMailClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Mail,
                    contentDescription = "Mail",
                    tint = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActionButtons(
    onGithubProjectClick: () -> Unit,
    onBugReportClick: () -> Unit,
    onSeeLicenseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = ButtonDefaults.squareShape,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            onClick = onGithubProjectClick
        ) {
            Icon(
                Icons.Default.Code,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.see_code_github))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = ButtonDefaults.squareShape,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            onClick = onBugReportClick
        ) {
            Icon(
                imageVector = Icons.Default.ReportProblem,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.report_bug_setting))
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            shape = ButtonDefaults.squareShape,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            onClick = onSeeLicenseClick
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.see_license))
        }
    }
}

