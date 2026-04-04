package th.skylabmek.kmp_frontend.features.app_features.profile.components.scale

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.domain.model.profile.Scale
import th.skylabmek.kmp_frontend.shared_resources.*

@Composable
fun ScaleIndicator(
    scaleId: String?,
    value: Double,
    modifier: Modifier = Modifier
) {
    val scale = Scale.fromId(scaleId)
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Context Label
        Text(
            text = getScaleLabel(scale),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
        )

        when (scale.id) {
            Scale.SCALE_CEFR.id -> CefrScale(value)
            Scale.SCALE_REFERENCE.id -> ReferenceScale()
            Scale.SCALE_1_5_INT.id -> DiscreteScale(value, 5)
            Scale.SCALE_1_10_INT.id -> DiscreteScale(value, 10)
            Scale.SCALE_FAMILIAR_TEXT.id -> FamiliarTextScale(value)
            Scale.UNKNOWN.id -> Spacer(Modifier.height(0.dp))
            else -> ProgressBarScale(scale, value)
        }
    }
}

@Composable
private fun getScaleLabel(scale: Scale): String {
    val res = when (scale.id) {
        Scale.SCALE_CEFR.id -> Res.string.scale_label_cefr
        Scale.SCALE_REFERENCE.id -> Res.string.scale_label_reference
        Scale.SCALE_1_5_INT.id -> Res.string.scale_label_rating_5
        Scale.SCALE_1_10_INT.id -> Res.string.scale_label_rating_10
        Scale.SCALE_0_10_DECIMAL.id -> Res.string.scale_label_score_10
        Scale.SCALE_0_100_INT.id -> Res.string.scale_label_percentage
        Scale.SCALE_0_5_DECIMAL.id -> Res.string.scale_label_score_5
        Scale.SCALE_FAMILIAR_10.id -> Res.string.scale_label_familiarity
        else -> Res.string.scale_label_default
    }
    return stringResource(res)
}

@Composable
private fun ProgressBarScale(scale: Scale, value: Double) {
    val range = (scale.max - scale.min).coerceAtLeast(1.0)
    val progress = ((value - scale.min) / range).toFloat().coerceIn(0f, 1f)
    val displayValue = if (scale.isDecimal) {
        value.toString()
    } else {
        value.toInt().toString()
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$displayValue / ${scale.max.toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DiscreteScale(value: Double, max: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(max) { index ->
            val isActive = (index + 1) <= value
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${value.toInt()} / $max",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CefrScale(value: Double) {
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
    val index = (value.toInt() - 1).coerceIn(0, 5)
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        levels.forEachIndexed { i, level ->
            val isActive = i <= index
            val isCurrent = i == index
            
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isCurrent) MaterialTheme.colorScheme.primary
                        else if (isActive) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimary
                    else if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReferenceScale() {
    SuggestionChip(
        onClick = {},
        label = { 
            Text(
                text = stringResource(Res.string.scale_reference_only),
                style = MaterialTheme.typography.labelSmall
            ) 
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
private fun FamiliarTextScale(value: Double) {
    val levels = listOf(
        1 to Res.string.scale_familiar_beginner,
        2 to Res.string.scale_familiar_familiar,
        3 to Res.string.scale_familiar_fluent,
        4 to Res.string.scale_familiar_deep
    )
    val current = value.toInt().coerceIn(1, 4)

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        levels.forEach { (level, label) ->
            val isActive = level <= current
            val isCurrent = level == current

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isActive  -> MaterialTheme.colorScheme.primaryContainer
                            else      -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isCurrent -> MaterialTheme.colorScheme.onPrimary
                        isActive  -> MaterialTheme.colorScheme.onPrimaryContainer
                        else      -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
