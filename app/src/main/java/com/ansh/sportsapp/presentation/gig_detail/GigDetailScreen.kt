package com.ansh.sportsapp.presentation.gig_detail

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.domain.model.GigStatus
import com.ansh.sportsapp.presentation.navigation.Screen
import com.ansh.sportsapp.presentation.review.ReviewViewModel
import com.ansh.sportsapp.presentation.review.SubmitReviewDialog
import com.ansh.sportsapp.presentation.review.SubmitReviewUiEvent
import com.ansh.sportsapp.ui.theme.*
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
            when (event) {
                is SubmitReviewUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is SubmitReviewUiEvent.Success -> {
                    snackbarHostState.showSnackbar("Review submitted!")
                    reviewingParticipant = null
                    reviewViewModel.resetState()
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is GigDetailUiEvent.JoinSuccess -> snackbarHostState.showSnackbar("Join request sent!")
                is GigDetailUiEvent.ShowSnackBar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    reviewingParticipant?.let { (participantId, username) ->
        SubmitReviewDialog(
            participantUsername = username,
            rating = reviewState.rating,
            comment = reviewState.comment,
            isSubmitting = reviewState.isSubmitting,
            onRatingChange = { reviewViewModel.onRatingChange(it) },
            onCommentChange = { reviewViewModel.onCommentChange(it) },
            onSubmit = { reviewViewModel.submitReview(state.gig!!.id, participantId) },
            onDismiss = { reviewingParticipant = null; reviewViewModel.resetState() }
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GigDetailTopBar(onBack = { navController.popBackStack() })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                state.isLoading -> DetailLoadingState()

                state.error != null -> DetailErrorState(message = state.error!!)

                else -> state.gig?.let { gig ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Hero card
                        item {
                            GigHeroCard(
                                gig = gig,
                                isOwner = state.isOwner,
                                onComplete = { viewModel.completeGig() }
                            )
                        }

                        // Action button
                        if (state.buttonState != JoinButtonState.HIDDEN) {
                            item {
                                GigActionButton(state = state, onJoin = { viewModel.onJoinClicked() })
                            }
                        }

                        // Chat button
                        if (state.isOwner || state.isParticipant) {
                            item {
                                GigChatButton(onClick = { navController.navigate("chat/${gig.id}") })
                            }
                        }

                        // Join requests (owner only)
                        if (state.buttonState == JoinButtonState.HIDDEN) {
                            item {
                                SectionHeader(title = "Join Requests", count = state.requests.size)
                            }
                            RequestsContent(
                                state = state,
                                onAccept = { viewModel.onAccept(it) },
                                onReject = { viewModel.onReject(it) },
                                onClick = { navController.navigate(Screen.UserProfile.createRoute(it)) }
                            )
                        }

                        // Review participants
                        if (state.isOwner && gig.status == GigStatus.COMPLETED) {
                            item {
                                SectionHeader(title = "Review Participants", count = gig.acceptedParticipants.size)
                            }
                            if (gig.acceptedParticipants.isEmpty()) {
                                item(key = "review_empty") { EmptyHint("No participants to review") }
                            } else {
                                items(
                                    items = gig.acceptedParticipants.toList(),
                                    key = { "p_${it.id}" }
                                ) { participant ->
                                    ParticipantReviewRow(
                                        participant = participant,
                                        onReviewClick = { id, username ->
                                            reviewingParticipant = id to username
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}