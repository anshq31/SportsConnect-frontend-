package com.ansh.sportsapp.presentation.gig_detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import com.ansh.sportsapp.domain.model.*
import com.ansh.sportsapp.presentation.gig_detail.*
import com.ansh.sportsapp.ui.theme.*
import kotlinx.coroutines.delay

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigDetailTopBar(onBack: () -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurface
                )
            }
            Text(
                text = "Gig Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = OnSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(SportGreen.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
        )
    }
}

// ─── Hero Card ────────────────────────────────────────────────────────────────

@Composable
fun GigHeroCard(
    gig: Gig,
    isOwner: Boolean,
    onComplete: () -> Unit
) {
    val statusColor = gigStatusColor(gig.status)
    val statusContainer = gigStatusContainer(gig.status)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceVariantDark)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(statusColor.copy(alpha = 0.3f), OutlineVariant)
                ),
                RoundedCornerShape(20.dp)
            )
    ) {
        // Color stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(statusColor, statusColor.copy(alpha = 0f))
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sport name + status chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = gig.sport.uppercase(),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        ),
                        color = OnSurface
                    )
                    Text(
                        text = "by @${gig.gigMasterUsername}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceHint
                    )
                }

                GigStatusChip(
                    status = gig.status,
                    isOwner = isOwner,
                    onComplete = onComplete
                )
            }

            // Divider
            Box(
                modifier = Modifier.fillMaxWidth().height(1.dp).background(OutlineVariant)
            )

            // Info rows
            DetailInfoRow(Icons.Default.LocationOn, "Location", gig.location, SportGreen)
            DetailInfoRow(Icons.Default.Event, "Date & Time", gig.dateTime.replace("T", " · "), TertiaryIndigo)
            DetailInfoRow(Icons.Default.Group, "Players needed", "${gig.playersNeeded}", WarningAmber)

            // Participants progress
            if (gig.acceptedParticipants.isNotEmpty()) {
                val progress = (gig.acceptedParticipants.size.toFloat() / gig.playersNeeded.toFloat()).coerceIn(0f, 1f)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Spots filled",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceHint
                        )
                        Text(
                            text = "${gig.acceptedParticipants.size} / ${gig.playersNeeded}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = SportGreen
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SportGreenContainer)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(2.dp))
                                .background(SportGreen)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GigStatusChip(status: GigStatus, isOwner: Boolean, onComplete: () -> Unit) {
    val statusColor = gigStatusColor(status)
    val statusContainer = gigStatusContainer(status)
    val canComplete = isOwner && (status == GigStatus.ACTIVE || status == GigStatus.FULL)

    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseAnim.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOut), RepeatMode.Reverse),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(statusContainer)
            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .then(if (canComplete) Modifier.clickable { onComplete() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (status == GigStatus.ACTIVE) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = pulseAlpha))
                )
            }
            Text(
                text = status.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = statusColor
            )
            if (canComplete) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceHint)
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = OnSurface)
        }
    }
}

// ─── Action button ────────────────────────────────────────────────────────────

@Composable
fun GigActionButton(state: GigDetailState, onJoin: () -> Unit) {
    when (state.buttonState) {
        JoinButtonState.CAN_JOIN -> {
            Button(
                onClick = onJoin,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !state.isJoinLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SportGreen,
                    contentColor = Color(0xFF0A0C0F)
                )
            ) {
                if (state.isJoinLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF0A0C0F),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.SportsSoccer, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Request to Join", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
        JoinButtonState.JOINED -> StatusChipButton("Joined", Icons.Default.CheckCircle, SportGreenContainer, SportGreen)
        JoinButtonState.PENDING -> StatusChipButton("Request Pending", Icons.Default.HourglassEmpty, WarningContainer, WarningAmber)
        JoinButtonState.REJECTED -> StatusChipButton("Request Rejected", Icons.Default.Cancel, ErrorContainer, ErrorRed)
        else -> Unit
    }
}

@Composable
private fun StatusChipButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bg: Color,
    fg: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, fg.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(18.dp))
            Text(text, color = fg, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

// ─── Chat button ──────────────────────────────────────────────────────────────

@Composable
fun GigChatButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(TertiaryContainer)
            .border(1.dp, TertiaryIndigo.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.AutoMirrored.Filled.Message, null, tint = TertiaryIndigo, modifier = Modifier.size(18.dp))
            Text("Open Team Chat", color = TertiaryIndigo, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, count: Int = -1) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(SportGreen)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = OnSurface
        )
        if (count > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SportGreenContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = SportGreen
                )
            }
        }
    }
}

// ─── Empty hint ───────────────────────────────────────────────────────────────

@Composable
fun EmptyHint(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariantDark)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceHint)
    }
}

// ─── Requests ────────────────────────────────────────────────────────────────

fun LazyListScope.RequestsContent(
    state: GigDetailState,
    onAccept: (Long) -> Unit,
    onReject: (Long) -> Unit,
    onClick: (Long) -> Unit
) {
    when {
        state.isRequestsLoading -> {
            item(key = "requests_loading") {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SportGreen, strokeWidth = 2.dp)
                }
            }
        }
        state.requests.isEmpty() -> {
            item(key = "requests_empty") { EmptyHint("No pending join requests") }
        }
        else -> {
            items(state.requests, key = { it.requestId }) { request ->
                SwipeableRequestCard(
                    request = request,
                    onAccept = { onAccept(request.requestId) },
                    onReject = { onReject(request.requestId) },
                    onClick = onClick
                )
            }
        }
    }
}

// ─── Swipeable Request Card ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableRequestCard(
    request: GigRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClick: (Long) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    var swipeAction by remember { mutableStateOf<SwipeAction?>(null) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> { swipeAction = SwipeAction.ACCEPT; isRemoved = true; true }
                SwipeToDismissBoxValue.EndToStart -> { swipeAction = SwipeAction.REJECT; isRemoved = true; true }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { it * 0.4f }
    )

    LaunchedEffect(isRemoved) {
        if (isRemoved) {
            delay(300)
            when (swipeAction) {
                SwipeAction.ACCEPT -> onAccept()
                SwipeAction.REJECT -> onReject()
                null -> Unit
            }
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(tween(300), Alignment.Top) + fadeOut(tween(300))
    ) {
        SwipeToDismissBox(
            state = dismissState,
            modifier = Modifier.fillMaxWidth(),
            backgroundContent = { SwipeBackground(dismissState) },
            content = { RequestCardContent(request = request, onClick = onClick) }
        )
    }
}

private enum class SwipeAction { ACCEPT, REJECT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(state: SwipeToDismissBoxState) {
    val isAccept = state.dismissDirection == SwipeToDismissBoxValue.StartToEnd
    val isReject = state.dismissDirection == SwipeToDismissBoxValue.EndToStart

    val bgColor by animateColorAsState(
        targetValue = when {
            isAccept -> SportGreen.copy(alpha = 0.15f)
            isReject -> ErrorRed.copy(alpha = 0.15f)
            else -> Color.Transparent
        },
        animationSpec = tween(150),
        label = "swipeBg"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(horizontal = 24.dp),
        contentAlignment = if (isAccept) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        if (isAccept || isReject) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = if (isAccept) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isAccept) SportGreen else ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = if (isAccept) "Accept" else "Reject",
                    color = if (isAccept) SportGreen else ErrorRed,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun RequestCardContent(request: GigRequest, onClick: (Long) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clickable { onClick(request.requesterId) }
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SportGreenContainer)
                        .border(1.dp, SportGreen.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = request.requesterUsername.first().uppercaseChar().toString(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = SportGreen
                    )
                }
                Column {
                    Text(
                        text = "@${request.requesterUsername}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = SportGreen,
                        textDecoration = TextDecoration.Underline
                    )
                    Text("Wants to join", style = MaterialTheme.typography.bodySmall, color = OnSurfaceHint)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, null, tint = ErrorRed.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                Icon(Icons.Default.KeyboardArrowRight, null, tint = SportGreen.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─── Participant review row ───────────────────────────────────────────────────

@Composable
fun ParticipantReviewRow(
    participant: Participant,
    onReviewClick: (Long, String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(TertiaryContainer)
                        .border(1.dp, TertiaryIndigo.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = participant.username.first().uppercaseChar().toString(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TertiaryIndigo
                    )
                }
                Text(
                    text = "@${participant.username}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = OnSurface
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(TertiaryContainer)
                    .border(1.dp, TertiaryIndigo.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .clickable { onReviewClick(participant.id, participant.username) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Review", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = TertiaryIndigo)
            }
        }
    }
}

// ─── Detail loading / error ───────────────────────────────────────────────────

@Composable
fun DetailLoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SportGreen, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
    }
}

@Composable
fun DetailErrorState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.ErrorOutline, null, tint = ErrorRed, modifier = Modifier.size(40.dp))
            Text(message, color = ErrorRed, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ─── Kept for backward compat ─────────────────────────────────────────────────

@Composable
fun GigStatusIndicator(status: GigStatus, onComplete: () -> Unit) {
    GigStatusChip(status = status, isOwner = true, onComplete = onComplete)
}

@Composable
fun GigActionSection(state: GigDetailState, onJoin: () -> Unit) {
    GigActionButton(state = state, onJoin = onJoin)
}

@Composable
fun ReceivedRequestsContent(
    state: GigDetailState,
    onAccept: (Long) -> Unit,
    onReject: (Long) -> Unit,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) { /* No-op - now handled via LazyListScope extension */ }