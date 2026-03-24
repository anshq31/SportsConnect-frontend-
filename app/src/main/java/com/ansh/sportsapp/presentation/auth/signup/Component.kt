package com.ansh.sportsapp.presentation.auth.signup


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.ansh.sportsapp.ui.theme.*

// ─── Step indicator for register flow ────────────────────────────────────────

@Composable
fun RegisterHeader() {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SportGreen.copy(alpha = 0.3f))
            )
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SportGreen)
            )
        }

        Text(
            text = "JOIN THE",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                fontSize = 38.sp
            ),
            color = OnSurface
        )
        Text(
            text = "SQUAD",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                fontSize = 38.sp
            ),
            color = SportGreen
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Create your player profile",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceHint
        )
    }
}

// ─── Strength indicator (password) ───────────────────────────────────────────

@Composable
fun PasswordStrengthBar(password: String) {
    val strength = when {
        password.length >= 12 && password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() } -> 3
        password.length >= 8 && password.any { it.isDigit() } -> 2
        password.length >= 6 -> 1
        else -> 0
    }

    val strengthColor = when (strength) {
        3 -> SportGreen
        2 -> WarningAmber
        1 -> ErrorRed
        else -> OutlineVariant
    }

    val strengthLabel = when (strength) {
        3 -> "Strong"
        2 -> "Medium"
        1 -> "Weak"
        else -> ""
    }

    if (password.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { i ->
                    val filled = i < strength
                    val color by animateColorAsState(
                        targetValue = if (filled) strengthColor else ElevatedDark,
                        animationSpec = tween(200),
                        label = "strength$i"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(color)
                    )
                }
            }
            if (strengthLabel.isNotEmpty()) {
                Text(
                    text = strengthLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = strengthColor
                )
            }
        }
    }
}

// ─── Terms checkbox ───────────────────────────────────────────────────────────

@Composable
fun SportDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, OutlineVariant)
                    )
                )
        )
        Text(
            text = "or",
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceHint
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(OutlineVariant, Color.Transparent)
                    )
                )
        )
    }
}