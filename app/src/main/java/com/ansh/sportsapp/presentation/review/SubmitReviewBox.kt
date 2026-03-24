package com.ansh.sportsapp.presentation.review

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.ansh.sportsapp.ui.theme.*

@Composable
fun SubmitReviewDialog(
    participantUsername: String,
    rating: Int,
    comment: String,
    isSubmitting: Boolean,
    onRatingChange: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceVariantDark)
                .border(1.dp, OutlineVariant, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ── Header ────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ElevatedDark)
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    // Left accent stripe
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(42.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SportGreen)
                            .align(Alignment.CenterStart)
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 14.dp)
                    ) {
                        Text(
                            text = "Leave a Review",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = OnSurface
                        )
                        Text(
                            text = "@$participantUsername",
                            style = MaterialTheme.typography.bodySmall,
                            color = SportGreen
                        )
                    }

                    // Close button
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariantDark)
                            .border(1.dp, OutlineVariant, CircleShape)
                            .then(
                                if (!isSubmitting) Modifier.clickable { onDismiss() }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = OnSurfaceHint,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(SportGreen.copy(alpha = 0.4f), OutlineVariant)
                            )
                        )
                )

                // ── Body ──────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {

                    // Star rating section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "How was your experience?",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceHint
                        )

                        // Star tiles
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (i in 1..5) {
                                StarTile(
                                    index = i,
                                    selected = i <= rating,
                                    onClick = { onRatingChange(i) }
                                )
                            }
                        }

                        // Rating label pill
                        AnimatedVisibility(
                            visible = rating > 0,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(WarningContainer)
                                    .border(
                                        1.dp,
                                        WarningAmber.copy(alpha = 0.3f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = when (rating) {
                                        1 -> "😕 Poor"
                                        2 -> "😐 Fair"
                                        3 -> "🙂 Good"
                                        4 -> "😊 Very Good"
                                        5 -> "🔥 Excellent"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = WarningAmber
                                )
                            }
                        }
                    }

                    // Comment field
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "COMMENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            ),
                            color = OnSurfaceHint
                        )
                        OutlinedTextField(
                            value = comment,
                            onValueChange = onCommentChange,
                            placeholder = {
                                Text(
                                    "Share your experience (optional)...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceDisabled
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            enabled = !isSubmitting,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = SportGreen,
                                unfocusedContainerColor = ElevatedDark,
                                focusedContainerColor = ElevatedDark,
                                cursorColor = SportGreen,
                                unfocusedTextColor = OnSurface,
                                focusedTextColor = OnSurface,
                                unfocusedPlaceholderColor = OnSurfaceDisabled,
                                focusedPlaceholderColor = OnSurfaceDisabled
                            ),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // ── Footer ────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ElevatedDark)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Cancel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceVariantDark)
                                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                                .then(
                                    if (!isSubmitting) Modifier.clickable { onDismiss() }
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (!isSubmitting) OnSurfaceVariant else OnSurfaceDisabled,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Submit
                        val canSubmit = !isSubmitting && rating > 0
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (canSubmit) SportGreen else SportGreenContainer
                                )
                                .border(
                                    1.dp,
                                    if (canSubmit) Color.Transparent
                                    else SportGreen.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .then(
                                    if (canSubmit) Modifier.clickable { onSubmit() } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = isSubmitting,
                                transitionSpec = {
                                    fadeIn(tween(150)) togetherWith fadeOut(tween(150))
                                },
                                label = "submitContent"
                            ) { submitting ->
                                if (submitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = SportGreen
                                    )
                                } else {
                                    Text(
                                        text = if (rating == 0) "Select a rating" else "Submit Review",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = if (canSubmit) Color(0xFF0A0C0F)
                                        else SportGreen.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Star tile ────────────────────────────────────────────────────────────────

@Composable
private fun StarTile(index: Int, selected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) WarningContainer else ElevatedDark,
        animationSpec = tween(150),
        label = "starBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) WarningAmber.copy(alpha = 0.5f) else OutlineVariant,
        animationSpec = tween(150),
        label = "starBorder"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "starScale"
    )

    Box(
        modifier = Modifier
            .size(46.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (selected) Icons.Default.Star else Icons.Default.StarBorder,
            contentDescription = "Star $index",
            tint = if (selected) WarningAmber else OnSurfaceDisabled,
            modifier = Modifier.size(24.dp)
        )
    }
}