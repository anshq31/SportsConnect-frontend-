package com.ansh.sportsapp.presentation.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.ui.theme.BackgroundDark

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

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onPermissionResult(granted)
    }

    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 60 }
    }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && isRefreshing) isRefreshing = false
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            HomeTopBar(
                isScrolled = isScrolled,
                sportQuery = state.sportQuery,
                nearMeActive = state.nearMeActive,
                hasLocationPermission = state.hasLocationPermission,
                radiusKm = state.radiusKm,
                onSportChange = viewModel::onSportQueryChange,
                onNearMeToggle = {
                    if (!state.hasLocationPermission) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    } else {
                        viewModel.toggleNearMe()
                    }
                },
                onRadiusChange = viewModel::setRadius,
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
                    hasFilters = state.sportQuery.isNotBlank() || state.nearMeActive
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
