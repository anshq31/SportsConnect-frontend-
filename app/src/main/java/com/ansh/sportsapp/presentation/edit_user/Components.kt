package com.ansh.sportsapp.presentation.edit_user

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.ansh.sportsapp.ui.theme.*

// ─── Top bar ─────────────────────────────────────────────────────────────────

@Composable
fun EditProfileTopBar(username: String?, onBack: () -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
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
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = "EDIT PROFILE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = SportGreen,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                if (username != null) {
                    Text(
                        text = "@$username",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceHint,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, SportGreen.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )
    }
}

// ─── Avatar header ────────────────────────────────────────────────────────────

@Composable
fun ProfileAvatarHeader(username: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(20.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(SportGreenContainer, ElevatedDark)
                        )
                    )
                    .border(2.dp, SportGreen.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = SportGreen
                )
            }

            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
        }
    }
}

// ─── Section card ─────────────────────────────────────────────────────────────

@Composable
fun EditSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Section header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ElevatedDark)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SportGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SportGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = OnSurface
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(OutlineVariant)
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

// ─── Skill chip (selectable) ──────────────────────────────────────────────────

@Composable
fun SelectableSkillChip(
    skill: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) TertiaryContainer else ElevatedDark,
        animationSpec = tween(180),
        label = "skillBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) TertiaryIndigo.copy(alpha = 0.5f) else OutlineVariant,
        animationSpec = tween(180),
        label = "skillBorder"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) TertiaryIndigo else OnSurfaceHint,
        animationSpec = tween(180),
        label = "skillText"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = TertiaryIndigo,
                modifier = Modifier.size(12.dp)
            )
        }
        Text(
            text = skill,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = textColor
        )
    }
}

// ─── Save button ──────────────────────────────────────────────────────────────

@Composable
fun SaveButton(isSaving: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isSaving) 0.97f else 1f,
        animationSpec = tween(150),
        label = "saveScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (!isSaving) SportGreen else SportGreenContainer)
            .border(
                1.dp,
                if (!isSaving) Color.Transparent else SportGreen.copy(alpha = 0.3f),
                RoundedCornerShape(14.dp)
            )
            .then(if (!isSaving) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isSaving,
            transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
            label = "saveContent"
        ) { saving ->
            if (saving) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = SportGreen,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Saving...",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = SportGreen
                    )
                }
            } else {
                Text(
                    text = "SAVE CHANGES",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFF0A0C0F)
                )
            }
        }
    }
}