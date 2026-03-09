package com.ansh.sportsapp.presentation.my_gigs


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.model.GigRequest
import com.ansh.sportsapp.presentation.gig_detail.GigDetailScreen
import com.ansh.sportsapp.presentation.gig_detail.GigDetailState
import com.ansh.sportsapp.presentation.home.GigInfoRow

@Composable
fun RequestCard(
    request: GigRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "@${request.requesterUsername}",
                        style = MaterialTheme.typography.titleMedium
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


@Composable
fun GigCard(
    gig: Gig,
    onItemClick :(Gig)-> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = {onItemClick(gig)}
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header: Sport & Host
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = gig.sport.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "@${gig.gigMasterUsername}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details
            GigInfoRow(icon = Icons.Default.LocationOn, text = gig.location)
            Spacer(modifier = Modifier.height(4.dp))
            GigInfoRow(icon = Icons.Default.Event, text = gig.dateTime.replace("T", " "))
            Spacer(modifier = Modifier.height(4.dp))
            GigInfoRow(icon = Icons.Default.Person, text = "Looking for ${gig.playersNeeded} players")
        }
    }
}


@Composable
fun ErrorState(
    message : String,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyState(
    title : String,
    subtitle : String,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun JoinedGigsContent(
    state: MyGigsState,
    navController: NavController,
    modifier: Modifier = Modifier
){
    Box(modifier = modifier){
        when {
            state.isLoading->{
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.error != null && state.joinedGigs.isEmpty()->{
                ErrorState(
                    message = state.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.joinedGigs.isEmpty() ->{
                EmptyState(
                    title = "No Joined Gigs",
                    subtitle = "Gigs you've joined will appear here",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else->{
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = state.joinedGigs,
                        key = { it.id }
                    ) { gig ->
                        GigCard(
                            gig = gig,
                            onItemClick = {navController.navigate("gig_detail/${gig.id}")}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreatedGigContent(
    state: MyGigsState,
    navController: NavController,
    modifier: Modifier = Modifier
){
    Box(modifier = modifier){
        when {
            state.isCreatedGigsLoading->{
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.error != null && state.createdGig.isEmpty()->{
                ErrorState(
                    message = state.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.createdGig.isEmpty() ->{
                EmptyState(
                    title = "No Joined Gigs",
                    subtitle = "Gigs you've joined will appear here",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else->{
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = state.createdGig,
                        key = { it.id }
                    ) { gig ->
                        GigCard(
                            gig = gig,
                            onItemClick = {navController.navigate("gig_detail/${gig.id}")}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReceivedRequestsContent(
    state: GigDetailState,
    onAccept: (Long) -> Unit,
    onReject: (Long) -> Unit,
    modifier: Modifier = Modifier
){

    Box(modifier = modifier){
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
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = state.requests,
                        key = { it.requestId }
                    ) { request ->
                        RequestCard(
                            request = request,
                            onAccept = { onAccept(request.requestId) },
                            onReject = { onReject(request.requestId) }
                        )
                    }
                }
            }
        }
    }

}