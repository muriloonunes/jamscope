package com.mno.jamscope.features.settings.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mno.jamscope.R
import com.mno.jamscope.ui.navigator.ScreenType
import com.mno.jamscope.util.Stuff.readRawFile
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadLibrariesLicenseScreen(
    screenType: ScreenType,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (screenType) {
                            ScreenType.LIBRARIES -> stringResource(R.string.show_libraries_setting)
                            ScreenType.LICENSE -> stringResource(R.string.see_license)
                        }
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
            )
        }
    ) { innerPadding ->
        when (screenType) {
            ScreenType.LIBRARIES -> {
                val libraries by rememberLibraries(R.raw.aboutlibraries)
                LibrariesContainer(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    showVersion = true,
                    libraries = libraries
                )
            }

            ScreenType.LICENSE -> {
                val scrollState = rememberLazyListState()
                LazyColumnScrollbar(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    state = scrollState,
                    settings = ScrollbarSettings(
                        thumbUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        thumbSelectedColor = MaterialTheme.colorScheme.onSurface,
                        scrollbarPadding = 0.dp
                    ),
                ) {
                    LazyColumn(
                        state = scrollState,
                        contentPadding = PaddingValues(bottom = 4.dp)
                    ) {
                        item {
                            val context = LocalContext.current
                            val licenseText = context.readRawFile(
                                fileRes = R.raw.gpl_license,
                                expectedFileName = "gpl_license"
                            )
                            Text(
                                text = licenseText,
                            )
                        }
                    }
                }
            }
        }
    }
}