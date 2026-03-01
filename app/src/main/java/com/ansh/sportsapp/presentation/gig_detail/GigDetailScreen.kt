package com.ansh.sportsapp.presentation.gig_detail


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.presentation.home.GigInfoRow
import com.ansh.sportsapp.presentation.my_gigs.ReceivedRequestsContent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigDetailScreen(
    navController: NavController,
    viewModel: GigDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is GigDetailUiEvent.JoinSuccess -> {
                    snackbarHostState.showSnackbar("Join request sent!")
                    // Optionally navigate back
                    // navController.popBackStack()
                }
                is GigDetailUiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gig Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                state.gig?.let { gig ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = gig.sport.uppercase(),
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hosted by @${gig.gigMasterUsername}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            GigInfoRow(Icons.Default.LocationOn, gig.location)
                            Spacer(modifier = Modifier.height(8.dp))
                            GigInfoRow(Icons.Default.Event, gig.dateTime.replace("T", " "))
                            Spacer(modifier = Modifier.height(8.dp))
                            GigInfoRow(Icons.Default.Person, "Players Needed: ${gig.playersNeeded}")
                        }

//                        Button(
//                            onClick = { viewModel.onJoinClicked() },
//                            modifier = Modifier.fillMaxWidth(),
//                            enabled = !state.isJoinLoading
//                        ) {
//                            if (state.isJoinLoading) {
//                                CircularProgressIndicator(
//                                    color = MaterialTheme.colorScheme.onPrimary,
//                                    modifier = Modifier.size(24.dp)
//                                )
//                            } else {
//                                Text("Request to Join")
//                            }
//                        }

                        Column(

                        ) {
                            when(state.buttonState){
                                JoinButtonState.HIDDEN -> {
                                    ReceivedRequestsContent(
                                        state = state,
                                        onAccept = {requestId->
                                            viewModel.onAccept(requestId)
                                        },
                                        onReject = {requestId->
                                            viewModel.onReject(requestId)
                                        },
                                        modifier = Modifier
                                    )
                                }
                                JoinButtonState.JOINED->{
                                    Button(
                                        onClick = {},
                                        enabled = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    ) {
                                        Text("✓ Joined")
                                    }
                                }

                                JoinButtonState.PENDING->{
                                    Button(
                                        onClick = {},
                                        enabled = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    ) {
                                        Text("⏳ Request Pending")
                                    }
                                }

                                JoinButtonState.REJECTED->{
                                    Button(
                                        onClick = {},
                                        enabled = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    ) {
                                        Text("✗ Request Rejected")
                                    }
                                }

                                JoinButtonState.CAN_JOIN ->{
                                    Button(
                                        onClick = { viewModel.onJoinClicked() },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = !state.isJoinLoading
                                    ) {
                                        if (state.isJoinLoading) {
                                            CircularProgressIndicator(
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Text("Request to Join")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = {
                                state.gig?.id?.let { id ->
                                    navController.navigate("chat/$id")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open Chat (Test)")
                        }
                    }
                }
            }
        }
    }
}