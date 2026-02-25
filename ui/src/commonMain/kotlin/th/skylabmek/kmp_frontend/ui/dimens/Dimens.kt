package th.skylabmek.kmp_frontend.ui.dimens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data class holding the dimension values.
 * Renamed to AppDimens to avoid conflict with the Dimens object.
 */
data class AppDimens(
    val spaceExtraSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceLarge: Dp = 24.dp,
    val spaceExtraLarge: Dp = 32.dp,

    val paddingExtraSmall: Dp = 4.dp,
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 16.dp,
    val paddingLarge: Dp = 24.dp,

    val itemSpacingSmall: Dp = 8.dp,
    val itemSpacingMedium: Dp = 16.dp,

    val screenPadding: Dp = 16.dp,
    val containerMaxWidth: Dp = 1200.dp,

    // Status indicator dimensions
    val statusDotSize: Dp = 12.dp,
    val statusDotSizeLarge: Dp = 24.dp,
    val statusDotBorderWidth: Dp = 2.dp,

    // Card dimensions
    val cardCornerRadius: Dp = 16.dp,
    val cardCornerRadiusSmall: Dp = 12.dp,
    val cardElevation: Dp = 8.dp,
    val cardElevationSmall: Dp = 4.dp,
    val cardMaxWidth: Dp = 400.dp,
    val cardPadding: Dp = 24.dp,

    // Popup/Tooltip dimensions
    val tooltipOffset: Dp = 24.dp,
    val tooltipMaxWidth: Dp = 300.dp
)

val LocalDimens = staticCompositionLocalOf { AppDimens() }

/**
 * Access the dimensions from the current theme.
 * Usage: Dimens.spaceMedium
 */
object Dimens {
    val spaceExtraSmall: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.spaceExtraSmall
    val spaceSmall: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.spaceSmall
    val spaceMedium: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.spaceMedium
    val spaceLarge: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.spaceLarge
    val spaceExtraLarge: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.spaceExtraLarge

    val paddingExtraSmall: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.paddingExtraSmall
    val paddingSmall: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.paddingSmall
    val paddingMedium: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.paddingMedium
    val paddingLarge: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.paddingLarge

    val itemSpacingSmall: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.itemSpacingSmall
    val itemSpacingMedium: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.itemSpacingMedium

    val screenPadding: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.screenPadding
    val containerMaxWidth: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.containerMaxWidth

    // Status indicator dimensions
    val statusDotSize: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.statusDotSize
    val statusDotSizeLarge: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.statusDotSizeLarge
    val statusDotBorderWidth: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.statusDotBorderWidth

    // Card dimensions
    val cardCornerRadius: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.cardCornerRadius
    val cardCornerRadiusSmall: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.cardCornerRadiusSmall
    val cardElevation: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.cardElevation
    val cardElevationSmall: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.cardElevationSmall
    val cardMaxWidth: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.cardMaxWidth
    val cardPadding: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.cardPadding

    // Popup/Tooltip dimensions
    val tooltipOffset: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.tooltipOffset
    val tooltipMaxWidth: Dp @Composable @ReadOnlyComposable get() = LocalDimens.current.tooltipMaxWidth
}

// Pre-defined dimension sets for different screen types
val CompactDimens = AppDimens(
    screenPadding = 16.dp,
    paddingMedium = 16.dp,
    cardPadding = 16.dp,
    statusDotSize = 14.dp,
    statusDotSizeLarge = 20.dp
)

val MediumDimens = AppDimens(
    screenPadding = 24.dp,
    paddingMedium = 24.dp,
    cardPadding = 24.dp
)

val ExpandedDimens = AppDimens(
    screenPadding = 32.dp,
    paddingMedium = 32.dp,
    cardPadding = 32.dp,
    statusDotSize = 18.dp,
    statusDotSizeLarge = 28.dp
)
