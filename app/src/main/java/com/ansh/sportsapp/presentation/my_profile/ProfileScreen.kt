package com.ansh.sportsapp.presentation.my_profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.domain.model.Review
import com.ansh.sportsapp.domain.model.UserProfile
import com.ansh.sportsapp.presentation.navigation.Screen
import com.ansh.sportsapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.LoggedOut -> { /* token watcher handles nav */ }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            ProfileTopBar(
                username = state.profile?.username,
                onEditClick = { navController.navigate(Screen.EditProfile.route) },
                onLogoutClick = { viewModel.logOut() }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true; viewModel.refresh() },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            LaunchedEffect(state.isLoading) {
                if (!state.isLoading && isRefreshing) {
                    delay(300); isRefreshing = false
                }
            }

            when {
                state.isLoading && state.profile == null -> ProfileLoadingState()

                state.error != null && state.profile == null -> ProfileErrorState(
                    message = state.error ?: "Something went wrong",
                    onRetry = { viewModel.refresh() }
                )

                state.profile != null -> ProfileContent(
                    profile = state.profile!!,
                    reviews = state.reviews,
                    isReviewLoading = state.isReviewLoading
                )
            }
        }
    }
}