package com.ansh.sportsapp.presentation.my_gigs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.presentation.home.GigCard
import com.ansh.sportsapp.presentation.my_gigs.MyGigsState
import com.ansh.sportsapp.ui.theme.*

// ─── Pill Tab Row ─────────────────────────────────────────────────────────────

@Composable
fun MyGigsPillTabs(
    selectedIndex: Int,
    createdCount: Int,
    joinedCount: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MyGigsTab(
            label = "Created",
            count = createdCount,
            selected = selectedIndex == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f)
        )
        MyGigsTab(
            label = "Joined",
            count = joinedCount,
            selected = selectedIndex == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MyGigsTab(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) SportGreenContainer else ElevatedDark,
        animationSpec = tween(200),
        label = "bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) SportGreen.copy(alpha = 0.5f) else OutlineVariant,
        animationSpec = tween(200),
        label = "border"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) SportGreen else OnSurfaceHint,
        animationSpec = tween(200),
        label = "text"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (selected) SportGreen else OutlineVariant)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) Color(0xFF0A0C0F) else OnSurfaceHint,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── Content areas ────────────────────────────────────────────────────────────

@Composable
fun CreatedGigContent(state: MyGigsState, navController: NavController) {
    when {
        state.isCreatedGigsLoading -> GigsLoadingState()
        state.createdGigError != null && state.createdGig.isEmpty() ->
            GigsErrorState(state.createdGigError)
        state.createdGig.isEmpty() ->
            GigsEmptyState(
                icon = Icons.Default.AddCircleOutline,
                title = "No created gigs",
                subtitle = "Create a game and invite others to join"
            )
        else -> LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { GigsCountHeader("${state.createdGig.size} created") }
            items(state.createdGig, key = { it.id }) { gig ->
                GigCard(gig = gig) { navController.navigate("gig_detail/${gig.id}") }
            }
        }
    }
}

@Composable
fun JoinedGigsContent(state: MyGigsState, navController: NavController) {
    when {
        state.isJoinedGigsLoading -> GigsLoadingState()
        state.joinedGigError != null && state.joinedGigs.isEmpty() ->
            GigsErrorState(state.joinedGigError)
        state.joinedGigs.isEmpty() ->
            GigsEmptyState(
                icon = Icons.Default.GroupAdd,
                title = "No joined gigs",
                subtitle = "Gigs you join will appear here"
            )
        else -> LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { GigsCountHeader("${state.joinedGigs.size} joined") }
            items(state.joinedGigs, key = { it.id }) { gig ->
                GigCard(gig = gig) { navController.navigate("gig_detail/${gig.id}") }
            }
        }
    }
}

// ─── Shared sub-composables ───────────────────────────────────────────────────

@Composable
fun GigsCountHeader(text: String) {
    Row(
        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(SportGreen)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceHint
        )
    }
}

@Composable
fun GigsLoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator(
                color = SportGreen,
                strokeWidth = 2.dp,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Loading gigs...",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceHint
            )
        }
    }
}

@Composable
fun GigsEmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ElevatedDark)
                    .border(1.dp, OutlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = OnSurfaceHint
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceHint
            )
        }
    }
}

@Composable
fun GigsErrorState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ErrorContainer)
                    .border(1.dp, ErrorRed.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = ErrorRed
                )
            }
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceHint
            )
        }
    }
}

// Kept for backward compat
@Composable
fun GigCard(gig: Gig, onItemClick: (Gig) -> Unit) {
    GigCard(gig = gig, onItemClick = onItemClick)
}

@Composable
fun CenterLoading() = GigsLoadingState()

@Composable
fun ErrorState(message: String, modifier: Modifier = Modifier) = GigsErrorState(message)

@Composable
fun EmptyState(title: String, subtitle: String, modifier: Modifier = Modifier) = GigsEmptyState(
    icon = Icons.Default.SportsSoccer, title = title, subtitle = subtitle
)