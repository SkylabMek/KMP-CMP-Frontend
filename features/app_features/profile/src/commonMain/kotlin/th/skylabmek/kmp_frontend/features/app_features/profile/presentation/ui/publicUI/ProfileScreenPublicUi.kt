package th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.publicUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.profile.Profile
import th.skylabmek.kmp_frontend.domain.model.profile.ProfileResult
import th.skylabmek.kmp_frontend.domain.model.profile.Skill
import th.skylabmek.kmp_frontend.domain.model.profile.Social
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfilePublicViewModel
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.ic_profile
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun ProfileScreenPublicUi(
    viewModel: ProfilePublicViewModel,
    profileId: String
) {
    val profileState by viewModel.profileState.collectAsState()

    LaunchedEffect(profileId) {
        viewModel.loadPublicProfile(profileId)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        when (val state = profileState) {
            is UiState.Loading -> DefaultLoadingContent(modifier = Modifier.fillMaxWidth().height(400.dp))
            is UiState.Error -> DefaultErrorContent(
                error = state.uiError,
                onRetry = { viewModel.loadPublicProfile(profileId) }
            )
            is UiState.Success -> {
                PublicProfileContent(data = state.data)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PublicProfileContent(data: ProfileResult) {
    val profile = data.profile
    val uriHandler = LocalUriHandler.current

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isDesktop = maxWidth > 800.dp
        val horizontalSpacing = Dimens.spaceLarge
        val verticalSpacing = Dimens.spaceLarge

        if (isDesktop) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing)
            ) {
                // Row 1: Header (Left) and Socials (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        HeaderSection(profile)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SocialsSection(data.socials, uriHandler)
                    }
                }

                // Row 2: Bio (Left) and Skills (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BioSection(profile.bio)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SkillsSection(data.skills)
                    }
                }
            }
        } else {
            // Mobile: Single column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.screenPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpacing)
            ) {
                HeaderSection(profile)
                BioSection(profile.bio)
                SkillsSection(data.skills)
                SocialsSection(data.socials, uriHandler)
            }
        }
    }
}

@Composable
private fun HeaderSection(profile: Profile) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
    ) {
        // Avatar
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = profile.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
                error = painterResource(Res.drawable.ic_profile),
                placeholder = painterResource(Res.drawable.ic_profile)
            )
            
            // Life Status Indicator
            profile.currentStatus?.let { status ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(status.color)
                    )
                }
            }
        }

        Text(
            text = profile.displayName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        profile.headline?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = Dimens.spaceMedium)
            )
        }
        
        profile.currentStatus?.let { status ->
             AssistChip(
                 onClick = {},
                 label = { Text(status.name) },
                 leadingIcon = {
                     Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(status.color))
                 }
             )
        }
    }
}

@Composable
private fun BioSection(bio: String?) {
    bio?.let {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.cardCornerRadius)
        ) {
            Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillsSection(skills: List<Skill>) {
    if (skills.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
        ) {
            Text(
                text = "Skills",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
            ) {
                skills.forEach { skill ->
                    SuggestionChip(
                        onClick = {},
                        label = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                skill.logoUrl?.let {
                                    AsyncImage(
                                        model = it,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(skill.name) 
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SocialsSection(socials: List<Social>, uriHandler: UriHandler) {
    if (socials.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
        ) {
            Text(
                text = "Connect",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                socials.forEach { social ->
                    IconButton(onClick = { 
                        try {
                            uriHandler.openUri(social.link)
                        } catch (e: Exception) {
                            // Handle invalid URI if necessary
                        }
                    }) {
                        AsyncImage(
                            model = social.logoUrl,
                            contentDescription = social.name,
                            modifier = Modifier.size(24.dp),
                            error = painterResource(Res.drawable.ic_profile),
                            placeholder = painterResource(Res.drawable.ic_profile)
                        )
                    }
                }
            }
        }
    }
}
