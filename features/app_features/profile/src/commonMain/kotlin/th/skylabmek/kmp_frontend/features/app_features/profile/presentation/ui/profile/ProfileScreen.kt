package th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.profile

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.auth.MeResult
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.domain.model.profile.Announce
import th.skylabmek.kmp_frontend.domain.model.profile.LifeStatus
import th.skylabmek.kmp_frontend.domain.model.profile.Profile
import th.skylabmek.kmp_frontend.features.app_features.profile.navigation.ProfileNavKey
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfileViewModel
import th.skylabmek.kmp_frontend.navigation.tools.NavigatorAccessor
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.ic_profile
import th.skylabmek.kmp_frontend.shared_resources.announcements_header

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    profileId: String
) {
    val navigator = NavigatorAccessor.current
    val meState by viewModel.meState.collectAsState()
    val basicDataState by viewModel.getOrLoadProfileBasicData(profileId).collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMe()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = meState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Not logged in or session expired",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navigator.navigate(ProfileNavKey.Login) }) {
                    Text("Login")
                }
            }
            is UiState.Success -> {
                val me = state.data
                when (val basicData = basicDataState) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is UiState.Success -> {
                        MeProfileContent(
                            me = me,
                            profile = basicData.data.profile,
                            onLogout = { viewModel.logout() },
                            onNavigateToPerformance = { 
                                val idToNavigate = basicData.data.profile?.id ?: me.id
                                navigator.navigate(ProfileNavKey.Performance(idToNavigate))
                            }
                        )
                    }
                    is UiState.Error -> {
                        MeProfileContent(
                            me = me,
                            profile = null,
                            onLogout = { viewModel.logout() },
                            onNavigateToPerformance = { navigator.navigate(ProfileNavKey.Performance(me.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MeProfileContent(
    me: MeResult,
    profile: Profile?,
    onLogout: () -> Unit,
    onNavigateToPerformance: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_profile),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        )

        Text(
            text = me.username,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = me.email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        profile?.id?.let {
            Text(
                text = "Profile ID: $it",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onNavigateToPerformance,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Performances")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Handle Refresh/Update */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update Profile")
                }

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    lifeStatus: LifeStatus,
    announces: List<Announce>,
    performances: List<Performance>
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LifeStatusCard(lifeStatus)
        
        if (announces.isNotEmpty()) {
            Text(stringResource(Res.string.announcements_header), style = MaterialTheme.typography.titleLarge)
            announces.forEach { announce ->
                AnnounceCard(announce)
            }
        }

        if (performances.isNotEmpty()) {
            // PerformanceSection(performances = performances)
        }
    }
}

@Composable
public fun LifeStatusCard(status: LifeStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_profile),
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray).padding(8.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(parseColor(status.colorToken))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(status.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                status.description?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun AnnounceCard(announce: Announce) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    announce.announceType.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Text(announce.createdAt, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            
            announce.title?.let {
                Text(it, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }
            
            announce.message?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            }

            if (announce.linkUrl != null) {
                TextButton(
                    onClick = { /* Handle Link */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(announce.linkText ?: "Read More")
                }
            }
        }
    }
}

fun parseColor(colorString: String): Color {
    return try {
        Color(colorString.removePrefix("#").toLong(16) or 0xFF000000)
    } catch (e: Exception) {
        Color.Gray
    }
}
