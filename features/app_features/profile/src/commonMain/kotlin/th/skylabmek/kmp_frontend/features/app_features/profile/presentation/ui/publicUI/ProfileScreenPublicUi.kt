package th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.publicUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
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
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.profile.*
import th.skylabmek.kmp_frontend.features.app_features.profile.components.common.BrandIcon
import th.skylabmek.kmp_frontend.features.app_features.profile.components.scale.ScaleIndicator
import th.skylabmek.kmp_frontend.features.app_features.profile.components.social.SocialIcon
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfilePublicViewModel
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.common.ComponentPreviewWrapper
import th.skylabmek.kmp_frontend.ui.common.DevicePreviews
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

@Composable
private fun PublicProfileContent(data: ProfileResult) {
    val profile = data.profile
    val uriHandler = LocalUriHandler.current

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isDesktop = maxWidth > 800.dp
        val horizontalSpacing = Dimens.spaceLarge
        val verticalSpacing = Dimens.spaceLarge

        if (isDesktop) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                // Left Column: Header, Bio, Socials
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HeaderSection(profile)
                    BioSection(profile.bio)
                    SocialsSection(data.socials, uriHandler)
                }

                // Right Column: Skills
                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                ) {
                    SkillsSection(data.skills)
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

        profile.contactEmail?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

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
                    text = stringResource(Res.string.profile_section_about),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun SkillsSection(skills: List<Skill>) {
    if (skills.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
        ) {
            Text(
                text = stringResource(Res.string.profile_section_skills),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                skills.forEach { skill ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BrandIcon(
                                logoUrl = skill.logoUrl,
                                domain = "${skill.name.lowercase().replace(" ", "")}.com",
                                contentDescription = skill.name,
                                size = 24.dp,
                                padding = 4.dp,
                                fallbackIcon = Icons.Default.Code
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = skill.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        ScaleIndicator(
                            scaleId = skill.scaleId,
                            value = skill.scaleValue,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )
                        
                        skill.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SocialsSection(socials: List<Social>, uriHandler: UriHandler) {
    if (socials.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.profile_section_connect),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            FlowRow(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                socials.forEach { social ->
                    SocialIcon(
                        social = social,
                        uriHandler = uriHandler
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
fun ProfileScreenPublicUiPreview() {
    val mockProfile = Profile(
        id = "profile_001",
        userId = "user_admin_001",
        displayName = "Praisarn Sormkaew",
        headline = "Software Developer",
        bio = "Personal website showcasing projects, skills, and achievements.",
        avatarUrl = null,
        contactEmail = "admin@example.com",
        createdAt = "2025-12-23",
        currentStatusId = "life_limited",
        currentStatus = LifeStatus(
            name = "Limited Availability",
            description = "Some availability with constraints",
            colorToken = "LIMITED"
        )
    )

    val mockSkills = listOf(
        Skill(
            id = "skill_lang_en",
            name = "English",
            skillType = "Communication Language",
            scaleId = "scale_cefr",
            scaleValue = 5.0,
            description = "Professional working proficiency"
        ),
        Skill(
            id = "skill_lang_th",
            name = "Thai",
            skillType = "Communication Language",
            scaleId = "scale_cefr",
            scaleValue = 6.0,
            description = "Native language"
        ),
        Skill(
            id = "skill_rust",
            name = "Rust",
            skillType = "Programming Language",
            scaleId = "scale_1_5_int",
            scaleValue = 4.0,
            description = "Backend development with Rust"
        ),
        Skill(
            id = "skill_sql",
            name = "SQL",
            skillType = "Programming Language",
            scaleId = "scale_1_5_int",
            scaleValue = 4.0,
            description = "Relational database design and queries"
        ),
        Skill(
            id = "skill_docker",
            name = "Docker",
            skillType = "Tool",
            scaleId = "scale_1_5_int",
            scaleValue = 3.0,
            description = "Containerized development"
        ),
        Skill(
            id = "skill_linux",
            name = "Linux",
            skillType = "Platform",
            scaleId = "scale_familiar_10",
            scaleValue = 7.5,
            description = "Daily development environment"
        ),
        Skill(
            id = "skill_teamwork",
            name = "Teamwork",
            skillType = "Soft Skill",
            scaleId = "scale_familiar_10",
            scaleValue = 8.0,
            description = "Collaborative project work"
        ),
        Skill(
            id = "skill_other_framework",
            name = "Other Framework",
            skillType = "Framework",
            scaleId = "scale_familiar_10",
            scaleValue = 1.0,
            description = "Experience with various frameworks"
        )
    )

    val mockScales = listOf(
        Scale.SCALE_0_10_DECIMAL,
        Scale.SCALE_0_100_INT,
        Scale.SCALE_0_5_DECIMAL,
        Scale.SCALE_1_10_INT,
        Scale.SCALE_1_5_INT,
        Scale.SCALE_CEFR,
        Scale.SCALE_FAMILIAR_10,
        Scale.SCALE_REFERENCE
    )

    val mockSocials = listOf(
        Social(id = "social_github", name = "GitHub", link = "https://github.com/yourusername"),
        Social(id = "social_linkedin", name = "LinkedIn", link = "https://linkedin.com/in/yourusername"),
        Social(id = "social_facebook", name = "Facebook", link = "https://facebook.com/yourusername"),
        Social(
            id = "social_discord",
            name = "Discord",
            link = "https://discord.com/users/720840047779381271",
            logoUrl = "https://storage.googleapis.com/personal-website_storage_dev/resource/social/logo/Discord%20Social.png"
        )
    )

    val mockData = ProfileResult(
        profile = mockProfile,
        skills = mockSkills,
        scales = mockScales,
        socials = mockSocials
    )

    ComponentPreviewWrapper {
        PublicProfileContent(data = mockData)
    }
}
