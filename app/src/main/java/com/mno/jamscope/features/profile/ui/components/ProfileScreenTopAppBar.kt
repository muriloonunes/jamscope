package com.mno.jamscope.features.profile.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.mno.jamscope.R
import com.mno.jamscope.ui.components.LastProBadge
import com.mno.jamscope.util.getCountryFlag
import com.mno.jamscope.util.getLocalizedCountryName
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenTopAppBar(
    imagePfp: Any?,
    username: String?,
    realName: String?,
    subscriber: Int?,
    profileUrl: String?,
    country: String?,
    playcount: Long?,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    MediumTopAppBar(
        title = {
            CollapsingProfileTitle(
                imagePfp = imagePfp,
                username = username,
                realName = realName,
                subscriber = subscriber,
                profileUrl = profileUrl,
                country = country,
                playcount = playcount,
                // 0f = expandido, 1f = totalmente colapsado
                collapsedFraction = scrollBehavior.state.collapsedFraction
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun CollapsingProfileTitle(
    imagePfp: Any?,
    username: String?,
    realName: String?,
    subscriber: Int?,
    profileUrl: String?,
    country: String?,
    playcount: Long?,
    collapsedFraction: Float,
) {
    val avatarExpanded = 90.dp
    val avatarCollapsed = 70.dp
    val currentAvatarSize by animateDpAsState(
        targetValue = lerp(avatarExpanded, avatarCollapsed, collapsedFraction),
        label = "avatarSize"
    )

    val nameExpanded = 22.sp
    val nameCollapsed = 18.sp
    val nameSize = lerp(nameExpanded, nameCollapsed, collapsedFraction)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        ProfileImage(
            currentImage = imagePfp,
            username = username,
            size = currentAvatarSize,
            shape = CircleShape,
            isLastPro = subscriber == 1
        )

        Spacer(Modifier.width(12.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            // Quando expandido mostra realname • username; quando colapsado, favorece username
            val showRealName = (realName?.isNotEmpty() == true)
            val showBoth = collapsedFraction < 0.6f && showRealName

            if (showBoth) {
                Text(
                    text = buildProfileTitleAnnotated(
                        realName = realName,
                        username = username,
                        profileUrl = profileUrl
                    ),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = nameSize,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                val displayName = when {
                    realName?.isNotEmpty() == true -> realName
                    username?.isNotEmpty() == true -> username
                    else -> "User"
                }
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = nameSize,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Badge (Last.fm Pro) aparece no modo expandido; pode mostrar versão compacta no colapsado se quiser
            if ((subscriber == 1) && collapsedFraction < 1f) {
                Spacer(Modifier.height(2.dp))
                LastProBadge()
            }

            //quero q apareça só quando expandido
            if (collapsedFraction < 0.6f) {
                country?.takeIf { it.isNotEmpty() && it != "None" }
                    ?.let { country ->
                        Text(
                            text = "${getLocalizedCountryName(country)} ${getCountryFlag(country)}",
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        )
                    }
                playcount?.let { userPlaycount ->
                    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                    val playcount = numberFormat.format(userPlaycount)
                    Text(
                        text = stringResource(R.string.scrobbles, playcount),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}