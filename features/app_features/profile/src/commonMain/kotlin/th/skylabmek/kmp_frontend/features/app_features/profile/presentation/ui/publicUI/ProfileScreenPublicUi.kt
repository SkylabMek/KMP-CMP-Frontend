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
import androidx.compose.ui.text.style.TextOverflow
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
    val avatarShape = RoundedCornerShape(Dimens.cardCornerRadius)
    
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
                    .clip(avatarShape)
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

@OptIn(ExperimentalLayoutApi::class)
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
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                skills.forEach { skill ->
                    SkillItem(skill)
                }
            }
        }
    }
}

@Composable
private fun SkillItem(skill: Skill) {
    Card(
        modifier = Modifier.widthIn(min = 160.dp, max = 280.dp),
        shape = RoundedCornerShape(Dimens.cardCornerRadiusSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.paddingSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceExtraSmall)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandIcon(
                    logoUrl = skill.logoUrl,
                    domain = "${skill.name.lowercase().replace(" ", "")}.com",
                    contentDescription = skill.name,
                    size = 40.dp, // Bigger icon
                    padding = 0.dp,
                    fallbackIcon = Icons.Default.Code
                )
                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            ScaleIndicator(
                scaleId = skill.scaleId,
                value = skill.scaleValue,
                modifier = Modifier.fillMaxWidth()
            )
            
            skill.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
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
            description = "Relational database design"
        )
    )

    val mockSocials = listOf(
        Social(id = "s1", name = "GitHub", link = "https://github.com"),
        Social(id = "s2", name = "LinkedIn", link = "https://linkedin.com")
    )

    ComponentPreviewWrapper {
        PublicProfileContent(
            data = ProfileResult(
                profile = mockProfile,
                skills = mockSkills,
                socials = mockSocials
            )
        )
    }
}
