package com.ansh.sportsapp.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ansh.sportsapp.domain.model.ChatMessage
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

import com.ansh.sportsapp.ui.theme.*

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
fun ChatTopBar(onBack: () -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElevatedDark)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                    .clickable { onBack() }
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Center - Title + status
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TEAM CHAT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = SportGreen
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ChatOnlineIndicator()
                    Text(
                        text = "Live",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceHint
                    )
                }
            }
        }

        // Gradient accent line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, SportGreen.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )
    }
}

// ─── Pulsing online indicator ─────────────────────────────────────────────────

@Composable
fun ChatOnlineIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "online")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "onlineAlpha"
    )
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(SportGreen.copy(alpha = pulseAlpha))
    )
}

// ─── Message Bubble ───────────────────────────────────────────────────────────

@Composable
fun MessageBubble(message: ChatMessage) {
    val isMe = message.isFromMe

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar for others
        if (!isMe) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(TertiaryContainer, ElevatedDark)
                        )
                    )
                    .border(1.dp, TertiaryIndigo.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.senderUsername.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TertiaryIndigo
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Sender name (only for others)
            if (!isMe) {
                Text(
                    text = "@${message.senderUsername}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TertiaryIndigo,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Bubble
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isMe) 18.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 18.dp
                        )
                    )
                    .background(
                        if (isMe) {
                            Brush.linearGradient(
                                listOf(
                                    SportGreen.copy(alpha = 0.85f),
                                    SportGreen.copy(alpha = 0.7f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(ElevatedDark, SurfaceVariantDark)
                            )
                        }
                    )
                    .border(
                        1.dp,
                        if (isMe) SportGreen.copy(alpha = 0.4f) else OutlineVariant,
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isMe) 18.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 18.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMe) Color(0xFF0A0C0F) else OnSurface
                )
            }

            // Timestamp
            Text(
                text = formatChatTimestamp(message.timeStamp),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = OnSurfaceDisabled,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Space for "me" side
        if (isMe) {
            Spacer(Modifier.width(4.dp))
        }
    }
}

// ─── Empty state ─────────────────────────────────────────────────────────────

@Composable
fun ChatEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(SportGreenContainer)
                    .border(1.dp, SportGreen.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Message,
                    contentDescription = null,
                    tint = SportGreen,
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text = "No messages yet",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                text = "Be the first to say something\nto your squad!",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceHint,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Date separator ───────────────────────────────────────────────────────────

@Composable
fun ChatDateSeparator(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(OutlineVariant)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(ElevatedDark)
                .border(1.dp, OutlineVariant, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceHint
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(OutlineVariant)
        )
    }
}

// ─── Input Bar ────────────────────────────────────────────────────────────────

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    val isFocused = remember { mutableStateOf(false) }
    val hasText = value.isNotBlank()

    val borderColor by animateColorAsState(
        targetValue = if (isFocused.value) SportGreen.copy(alpha = 0.5f) else OutlineVariant,
        animationSpec = tween(200),
        label = "inputBorder"
    )

    Column {
        // Top border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(OutlineVariant)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .imePadding()
                .navigationBarsPadding(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Text field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(ElevatedDark)
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused.value = it.isFocused },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                    cursorBrush = SolidColor(SportGreen),
                    maxLines = 4,
//                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
//                    keyboardActions = KeyboardActions(onSend = { if (hasText) onSend() }),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = "Message your squad...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceDisabled
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // Send button
            val sendBg by animateColorAsState(
                targetValue = if (hasText) SportGreen else ElevatedDark,
                animationSpec = tween(200),
                label = "sendBg"
            )
            val sendScale by animateFloatAsState(
                targetValue = if (hasText) 1f else 0.9f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "sendScale"
            )

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .scale(sendScale)
                    .clip(CircleShape)
                    .background(sendBg)
                    .border(
                        1.dp,
                        if (hasText) Color.Transparent else OutlineVariant,
                        CircleShape
                    )
                    .then(if (hasText) Modifier.clickable { onSend() } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (hasText) Color(0xFF0A0C0F) else OnSurfaceDisabled,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

private fun formatChatTimestamp(raw: String): String {
    return try {
        // raw is typically ISO: "2026-03-24T10:30:00" or similar
        val timePart = raw.substringAfter("T").take(5)
        timePart
    } catch (e: Exception) {
        raw.take(5)
    }
}