package com.ansh.sportsapp.presentation.blocked_users

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
import com.ansh.sportsapp.data.local.BlockedUserEntry
import com.ansh.sportsapp.ui.theme.*

// ─── Blocked user row ─────────────────────────────────────────────────────────

@Composable
fun BlockedUserRow(
    entry: BlockedUserEntry,
    isUnblocking: Boolean,
    onUnblock: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ElevatedDark)
                    .border(1.dp, OutlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.username.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurfaceVariant
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = "@${entry.username}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = OnSurface,
                modifier = Modifier.weight(1f)
            )

            if (isUnblocking) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = SportGreen
                )
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SportGreenContainer)
                        .border(1.dp, SportGreen.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .clickable { onUnblock() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Unblock",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = SportGreen
                    )
                }
            }
        }
    }
}

// ─── Empty state ──────────────────────────────────────────────────────────────

@Composable
fun BlockedUsersEmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(ElevatedDark)
                    .border(1.dp, OutlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Block,
                    contentDescription = null,
                    tint = OnSurfaceHint,
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                "No blocked users",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                "Users you block will appear here.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceHint
            )
        }
    }
}
