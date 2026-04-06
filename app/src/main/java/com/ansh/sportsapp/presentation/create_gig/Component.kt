package com.ansh.sportsapp.presentation.create_gig


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.ansh.sportsapp.ui.theme.*

// ─── Top bar ─────────────────────────────────────────────────────────────────

@Composable
fun CreateGigTopBar(onBack: () -> Unit) {
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
                    text = "CREATE GIG",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = SportGreen,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Set up your game",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceHint,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
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

// ─── Step section header ──────────────────────────────────────────────────────

@Composable
fun StepSectionHeader(step: Int, title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Step number badge
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SportGreenContainer)
                .border(1.dp, SportGreen.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.toString(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = SportGreen
            )
        }
        Icon(icon, contentDescription = null, tint = OnSurfaceHint, modifier = Modifier.size(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = OnSurface
        )
    }
}

// ─── Sport chip selector ──────────────────────────────────────────────────────

@Composable
fun SportChipSelector(
    sports: List<Pair<Long, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        StepSectionHeader(1, "Sport", Icons.Default.SportsSoccer)

        // 2-column grid
        val rows = sports.chunked(3)
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (_, sportName) ->
                    val isSelected = selected.equals(sportName, ignoreCase = true)
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) SportGreenContainer else ElevatedDark,
                        animationSpec = tween(180),
                        label = "sportBg"
                    )
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) SportGreen.copy(alpha = 0.6f) else OutlineVariant,
                        animationSpec = tween(180),
                        label = "sportBorder"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) SportGreen else OnSurfaceVariant,
                        animationSpec = tween(180),
                        label = "sportText"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgColor)
                            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                            .clickable { onSelect(sportName) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sportName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = textColor
                        )
                    }
                }
                // Fill remaining cells if row is not full
                repeat(3 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// ─── Gig form text field ──────────────────────────────────────────────────────

@Composable
fun GigFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = OnSurfaceHint
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, style = MaterialTheme.typography.bodySmall, color = OnSurfaceDisabled)
            },
            leadingIcon = {
                Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = OutlineVariant,
                focusedBorderColor = SportGreen,
                unfocusedContainerColor = ElevatedDark,
                focusedContainerColor = ElevatedDark,
                cursorColor = SportGreen,
                unfocusedTextColor = OnSurface,
                focusedTextColor = OnSurface,
                unfocusedPlaceholderColor = OnSurfaceDisabled,
                focusedPlaceholderColor = OnSurfaceDisabled,
                unfocusedLeadingIconColor = OnSurfaceHint,
                focusedLeadingIconColor = SportGreen
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}