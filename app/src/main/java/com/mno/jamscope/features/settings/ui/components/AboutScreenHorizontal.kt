package com.mno.jamscope.features.settings.ui.components

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
import com.mno.jamscope.features.settings.ui.ActionButtons
import com.mno.jamscope.features.settings.ui.AppDescriptionCard
import com.mno.jamscope.features.settings.ui.AppInfoHeader
import com.mno.jamscope.features.settings.ui.AuthorCard

@Composable
fun AboutScreenHorizontal(
    modifier: Modifier = Modifier,
    versionName: String,
    onGithubProfileClick: () -> Unit,
    onMailClick: () -> Unit,
    onGithubProjectClick: () -> Unit,
    onBugReportClick: () -> Unit,
    onSeeLicenseClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxSize()
    ) {
        // Painel Esquerdo: Informações do App
        AppInfoHeader(
            versionName = versionName,
            verticalArrangement = Arrangement.Center, // Centraliza verticalmente
            modifier = Modifier
                .weight(1f) // Ocupa metade da largura
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    // Muda o shape para arredondar a lateral
                    shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
                )
                .padding(horizontal = 16.dp)
        )

        // Painel Direito: Cards e Botões
        LazyColumn(
            modifier = Modifier.weight(1f), // Ocupa a outra metade
            verticalArrangement = Arrangement.spacedBy(16.dp),
            // Adiciona padding para os cards não colarem nas bordas
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