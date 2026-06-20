package com.ansh.sportsapp.presentation.user_profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.presentation.my_profile.ProfileContent
import com.ansh.sportsapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showBlockMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UserProfileUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(state.profile?.username ?: "User Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isBlockedByMe) {
                        Box {
                            IconButton(onClick = { showBlockMenu = true }) {
                                if (state.isBlockLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = OnSurfaceVariant
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "More options",
                                        tint = OnSurfaceVariant
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = showBlockMenu,
                                onDismissRequest = { showBlockMenu = false },
                                containerColor = SurfaceDark
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Unblock User",
                                            color = SportGreen,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Block,
                                            contentDescription = null,
                                            tint = SportGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    onClick = {
                                        showBlockMenu = false
                                        viewModel.unblockUser()
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refresh()
            },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LaunchedEffect(state.isLoading) {
                if (!state.isLoading && isRefreshing) {
                    delay(300)
                    isRefreshing = false
                }
            }

            when {
                state.isLoading && state.profile == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SportGreen, strokeWidth = 2.dp)
                    }
                }

                state.isBlockedAccess -> {
                    ProfileBlockedAccessState()
                }

                state.error != null && state.profile == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.error ?: "Something went wrong",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                        }
                    }
                }

                state.profile != null -> {
                    Column {
                        if (state.isBlockedByMe) {
                            BlockedBanner()
                        }
                        ProfileContent(
                            profile = state.profile!!,
                            reviews = state.reviews,
                            isReviewLoading = state.isReviewLoading
                        )
                    }
                }
            }
        }
    }
}

