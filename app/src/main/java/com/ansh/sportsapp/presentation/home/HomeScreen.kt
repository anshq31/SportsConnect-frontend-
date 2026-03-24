package com.ansh.sportsapp.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 60 }
    }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && isRefreshing) isRefreshing = false
    }

    Scaffold(
        containerColor = com.ansh.sportsapp.ui.theme.BackgroundDark,
        topBar = {
            HomeTopBar(
                isScrolled = isScrolled,
                sportQuery = state.sportQuery,
                locationQuery = state.locationQuery,
                onSportChange = viewModel::onSportQueryChange,
                onLocationChange = viewModel::onLocationQueryChange,
                onClearFilters = viewModel::clearFilters
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true; viewModel.refresh() },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading && state.gigs.isEmpty() -> HomeLoadingState()

                state.error != null && state.gigs.isEmpty() -> HomeErrorState(
                    message = state.error ?: "Something went wrong",
                    onRetry = { viewModel.refresh() }
                )

                state.gigs.isEmpty() -> HomeEmptyState(
                    hasFilters = state.sportQuery.isNotBlank() || state.locationQuery.isNotBlank()
                )

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp,
                            top = 12.dp, bottom = 32.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            GigCountBadge(count = state.gigs.size)
                        }
                        items(state.gigs, key = { it.id }) { gig ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300)) + slideInVertically(
                                    tween(300),
                                    initialOffsetY = { it / 4 }
                                )
                            ) {
                                GigCard(
                                    gig = gig,
                                    onItemClick = { navController.navigate("gig_detail/${gig.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}