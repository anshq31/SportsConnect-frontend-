package com.ansh.sportsapp.presentation.gig_detail


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.domain.model.GigStatus
import com.ansh.sportsapp.domain.model.Participant
import com.ansh.sportsapp.presentation.home.GigInfoRow
import com.ansh.sportsapp.presentation.my_gigs.ReceivedRequestsContent
import com.ansh.sportsapp.presentation.navigation.Screen
import com.ansh.sportsapp.presentation.review.ReviewViewModel
import com.ansh.sportsapp.presentation.review.SubmitReviewDialog
import com.ansh.sportsapp.presentation.review.SubmitReviewUiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigDetailScreen(
    navController: NavController,
    viewModel: GigDetailViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val reviewState by reviewViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var reviewingParticipant by remember { mutableStateOf<Pair<Long, String>?>(null) }

    LaunchedEffect(Unit) {
        reviewViewModel.uiEvent.collectLatest { event ->
            when(event){
                is SubmitReviewUiEvent.ShowSnackbar->{
                    snackbarHostState.showSnackbar(event.message)
                }
                is SubmitReviewUiEvent.Success-> {
                    snackbarHostState.showSnackbar("Review submitted successfully!")
                    reviewingParticipant = null
                    reviewViewModel.resetState()
                }
            }
        }
    }

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


    reviewingParticipant?.let {(participantId, username)->
        SubmitReviewDialog(
            participantUsername = username,
            rating = reviewState.rating,
            comment = reviewState.comment,
            isSubmitting = reviewState.isSubmitting,
            onRatingChange = { reviewViewModel.onRatingChange(it) },
            onCommentChange = { reviewViewModel.onCommentChange(it) },
            onSubmit = { reviewViewModel.submitReview(state.gig!!.id, participantId = participantId) },
            onDismiss = {
                reviewingParticipant = null
                reviewViewModel.resetState() }
        )

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
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                 state.error != null-> {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else ->{
                    state.gig?.let { gig ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            item {

                                // HERO CARD
                                Card(
                                    shape = MaterialTheme.shapes.large,
                                    elevation = CardDefaults.cardElevation(6.dp)
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                    ) {

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {

                                            Text(
                                                text = gig.sport.uppercase(),
                                                style = MaterialTheme.typography.displaySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            GigStatusIndicator(
                                                status = gig.status,
                                                onComplete = { viewModel.completeGig() }
                                            )
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        Text(
                                            "Hosted by @${gig.gigMasterUsername}",
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Spacer(Modifier.height(16.dp))

                                        GigInfoRow(Icons.Default.LocationOn, gig.location)

                                        Spacer(Modifier.height(8.dp))

                                        GigInfoRow(
                                            Icons.Default.Event,
                                            gig.dateTime.replace("T", " ")
                                        )

                                        Spacer(Modifier.height(8.dp))

                                        GigInfoRow(
                                            Icons.Default.Person,
                                            "Players Needed: ${gig.playersNeeded}"
                                        )
                                    }
                                }
                            }

                            // REQUESTS SECTION
                            if (state.buttonState == JoinButtonState.HIDDEN) {

                                item {
                                    Text(
                                        "Join Requests",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }

                                item {

                                    ReceivedRequestsContent(
                                        state = state,
                                        onAccept = { viewModel.onAccept(it) },
                                        onReject = { viewModel.onReject(it) },
                                        onClick = {requesterId->
                                            navController.navigate(Screen.UserProfile.createRoute(requesterId))
                                        },
                                    )
                                }
                            }

                            // ACTION SECTION
                            item {
                                GigActionSection(
                                    state = state,
                                    onJoin = { viewModel.onJoinClicked() }
                                )
                            }

                            if (state.isOwner && state.gig?.status == GigStatus.COMPLETED){
                                item(key = "review_header") {
                                    Text(
                                        text = "Review Participants",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }

                                if (state.gig!!.acceptedParticipants.isEmpty()){
                                    item(key = "review_empty") {
                                        Text(
                                            text = "No participants to review",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }else{
                                    items(
                                        items = state.gig!!.acceptedParticipants.toList(),
                                        key = {"participant_${it.id}"}
                                    ){participant->
                                        ParticipantReviewRow(
                                            participant = participant ,
                                            onReviewClick = { id,username->
                                                reviewingParticipant = id to username
                                            }
                                        )
                                    }
                                }
                            }

                            // CHAT BUTTON
                            if (state.isOwner || state.isParticipant) {

                                item {

                                    Button(
                                        onClick = {
                                            navController.navigate("chat/${gig.id}")
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("💬 Open Team Chat")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// presentation/review/ParticipantReviewRow.kt
@Composable
fun ParticipantReviewRow(
    participant: Participant,
    onReviewClick: (Long, String) -> Unit  // you'll need participantId — see note below
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Text(
                    text = "@${participant.username}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            OutlinedButton(onClick = { onReviewClick(participant.id,participant.username) }) {
                Text("Review")
            }
        }
    }
}