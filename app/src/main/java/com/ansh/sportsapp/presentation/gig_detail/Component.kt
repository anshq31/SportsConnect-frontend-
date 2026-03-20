package com.ansh.sportsapp.presentation.gig_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ansh.sportsapp.domain.model.GigRequest
import com.ansh.sportsapp.domain.model.GigStatus
import com.ansh.sportsapp.presentation.my_gigs.EmptyState
import com.ansh.sportsapp.presentation.my_gigs.ErrorState

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

@Composable
fun ReceivedRequestsContent(
    state: GigDetailState,
    onAccept: (Long) -> Unit,
    onReject: (Long) -> Unit,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
){

    Box(modifier = modifier.fillMaxWidth()){
        when{
            state.isRequestsLoading->{
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.error!=null && state.requests.isEmpty()->{
                ErrorState(
                    message = state.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.requests.isEmpty()->{
                EmptyState(
                    title = "No Pending Requests",
                    subtitle = "Requests to join your gigs will appear here",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else->{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    state.requests.forEach { request ->
                        RequestCard(
                            request = request,
                            onAccept = { onAccept(request.requestId) },
                            onReject = { onReject(request.requestId) },
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun RequestCard(
    request: GigRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClick : (Long)-> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable{ onClick(request.requesterId)}.padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "@${request.requesterUsername}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                    Text(
                        text = "Wants to join",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row {
                IconButton(onClick = onReject) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Reject",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                IconButton(onClick = onAccept) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = Color(0xFF4CAF50) // Green
                    )
                }
            }
        }
    }
}
