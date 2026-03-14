package com.ansh.sportsapp.presentation.gig_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ansh.sportsapp.domain.model.GigStatus

@Composable
fun GigStatusIndicator(
    status: GigStatus,
    onComplete : () -> Unit
) {
    val isClickable = status == GigStatus.ACTIVE || status == GigStatus.FULL

    val label = when (status) {
        GigStatus.ACTIVE -> "ACTIVE"
        GigStatus.FULL -> "FULL"
        GigStatus.COMPLETED -> "COMPLETED"
        GigStatus.EXPIRED -> "EXPIRED"
    }

    val containerColor = when (status) {
        GigStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
        GigStatus.FULL -> MaterialTheme.colorScheme.secondaryContainer
        GigStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
        GigStatus.EXPIRED -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (status) {
        GigStatus.ACTIVE -> MaterialTheme.colorScheme.onPrimaryContainer
        GigStatus.FULL -> MaterialTheme.colorScheme.onSecondaryContainer
        GigStatus.COMPLETED -> MaterialTheme.colorScheme.onTertiaryContainer
        GigStatus.EXPIRED -> MaterialTheme.colorScheme.onErrorContainer
    }

    AssistChip(
        onClick = {if (isClickable) onComplete()},
        enabled = isClickable,
        label = {
            Text(label)
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        ),
        border = BorderStroke(width = 1.dp,contentColor)
    )
}

@Composable
fun GigActionSection(
    state: GigDetailState,
    onJoin: () -> Unit
) {

    when (state.buttonState) {

        JoinButtonState.CAN_JOIN -> {

            Button(
                onClick = onJoin,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isJoinLoading
            ) {

                if (state.isJoinLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Request to Join")
                }
            }
        }

        JoinButtonState.JOINED -> StatusButton("✓ Joined",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer)

        JoinButtonState.PENDING -> StatusButton("⏳ Request Pending",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer)

        JoinButtonState.REJECTED -> StatusButton("✗ Request Rejected",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer)

        else -> Unit
    }
}

@Composable
fun StatusButton(
    text: String,
    container: Color,
    content: Color
) {

    Button(
        onClick = {},
        enabled = false,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = container,
            disabledContentColor = content
        )
    ) {
        Text(text)
    }
}