package th.skylabmek.kmp_frontend.features.app_features.profile.components.skills

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.domain.model.profile.Skill
import th.skylabmek.kmp_frontend.features.app_features.profile.components.common.BrandIcon
import th.skylabmek.kmp_frontend.features.app_features.profile.components.scale.ScaleIndicator
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.profile_section_skills
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsSection(
    skills: List<Skill>,
    modifier: Modifier = Modifier
) {
    if (skills.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
    ) {
        Text(
            text = stringResource(Res.string.profile_section_skills),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
            maxItemsInEachRow = 2
        ) {
            skills.forEach { skill ->
                val defaultIcon = when (skill.skillType.lowercase()) {
                    "communication language", "language" -> Icons.Default.Language
                    "soft skill" -> Icons.Default.Person
                    "tool" -> Icons.Default.Build
                    "platform" -> Icons.Default.DesktopWindows
                    else -> Icons.Default.Code
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandIcon(
                            logoUrl = skill.logoUrl,
                            domain = if (skill.logoUrl == null) {
                                "${skill.name.lowercase().replace(" ", "")}.com"
                            } else null,
                            contentDescription = skill.name,
                            size = 24.dp,
                            padding = 0.dp,
                            fallbackIcon = defaultIcon
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = skill.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
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
