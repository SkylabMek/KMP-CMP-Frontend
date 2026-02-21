package th.skylabmek.kmp_frontend.features.app_features.profile.components.lifeStatus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipScope
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.common.errorMessage
import th.skylabmek.kmp_frontend.domain.model.profile.LifeStatus
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

// Larger clickable area (48dp minimum touch target for accessibility)
val clickableAreaSize = 42.dp

/**
 * Version 1: Shows a small Tooltip Card near the indicator.
 * Now uses Material 3 TooltipBox and RichTooltip.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeStatusIndicatorWithTooltip(
    uiState: UiState<LifeStatus>,
    modifier: Modifier = Modifier
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        tooltip = {
            LifeStatusTooltipContent(uiState)
        },
        state = tooltipState,
        modifier = modifier
    ) {
        LifeStatusDot(
            uiState = uiState,
            onClick = {
                scope.launch { tooltipState.show() }
            }
        )
    }
}

/**
 * Version 2: Shows a full-screen Popup overlay.
 */
@Composable
fun LifeStatusIndicatorWithPopup(
    uiState: UiState<LifeStatus>,
    modifier: Modifier = Modifier
) {
    BaseLifeStatusIndicator(
        uiState = uiState,
        modifier = modifier
    ) { onDismiss ->
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true)
        ) {
            LifeStatusPopupContent(uiState, onDismiss)
        }
    }
}

// --- Shared Base Components ---

@Composable
private fun BaseLifeStatusIndicator(
    uiState: UiState<LifeStatus>,
    modifier: Modifier = Modifier,
    overlayContent: @Composable (onDismiss: () -> Unit) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        LifeStatusDot(
            uiState = uiState,
            onClick = { isExpanded = !isExpanded }
        )

        if (isExpanded) {
            overlayContent { isExpanded = false }
        }
    }
}

@Composable
private fun LifeStatusDot(
    uiState: UiState<LifeStatus>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(clickableAreaSize)
            .clip(RoundedCornerShape(Dimens.cardCornerRadiusSmall))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .size(Dimens.statusDotSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.statusDotSize),
                    strokeWidth = 2.dp
                )
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .size(Dimens.statusDotSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "!",
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            is UiState.Success -> {
                Box(
                    modifier = Modifier
                        .size(Dimens.statusDotSize)
                        .clip(CircleShape)
                        .background(uiState.data.color)
                        .border(
                            width = Dimens.statusDotBorderWidth,
                            color = uiState.data.color.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}


// --- Content Layouts ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TooltipScope.LifeStatusTooltipContent(
    uiState: UiState<LifeStatus>
) {
    when (uiState) {
        is UiState.Loading -> {
            RichTooltip(
                title = {
                    Text(
                        text = "Loading",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Text(text = "Loading...", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        is UiState.Error -> {
            RichTooltip(
                title = {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            ) {
                Text(
                    text = uiState.uiError.errorMessage()(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        is UiState.Success -> {
            val lifeStatus = uiState.data
            RichTooltip(
                title = {
                    Text(
                        text = lifeStatus.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            ) {
                lifeStatus.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LifeStatusPopupContent(
    uiState: UiState<LifeStatus>,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        )

        Card(
            modifier = Modifier
                .padding(Dimens.spaceLarge)
                .widthIn(max = Dimens.cardMaxWidth),
            shape = RoundedCornerShape(Dimens.cardCornerRadius),
            elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
        ) {
            Column(modifier = Modifier.padding(Dimens.cardPadding)) {
                when (uiState) {
                    is UiState.Loading -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                            Text(
                                text = "Loading status...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    is UiState.Error -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacingSmall)
                        ) {
                            Box(
                                modifier = Modifier.size(Dimens.statusDotSizeLarge)
                                    .clip(CircleShape).background(MaterialTheme.colorScheme.error),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "!",
                                    color = MaterialTheme.colorScheme.onError,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        Text(
                            text = uiState.uiError.errorMessage()(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    is UiState.Success -> {
                        val lifeStatus = uiState.data
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacingSmall)
                        ) {
                            Box(
                                modifier = Modifier.size(Dimens.statusDotSizeLarge)
                                    .clip(CircleShape).background(lifeStatus.color)
                            )
                            Text(
                                text = lifeStatus.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        lifeStatus.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close")
                }
            }
        }
    }
}
