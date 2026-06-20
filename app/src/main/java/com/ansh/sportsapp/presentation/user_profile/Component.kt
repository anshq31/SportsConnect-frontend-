package com.ansh.sportsapp.presentation.user_profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ansh.sportsapp.ui.theme.*

// ─── Profile blocked-access state ────────────────────────────────────────────

@Composable
fun ProfileBlockedAccessState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Block,
                contentDescription = null,
                tint = OnSurfaceHint,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Profile not available",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                text = "This profile is not accessible.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = OnSurfaceHint
            )
        }
    }
}

// ─── Blocked banner ───────────────────────────────────────────────────────────

@Composable
fun BlockedBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ErrorContainer)
            .border(
                width = 1.dp,
                color = ErrorRed.copy(alpha = 0.25f),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Block,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "You have blocked this user. Tap ⋮ to unblock.",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = ErrorRed
            )
        }
    }
}
