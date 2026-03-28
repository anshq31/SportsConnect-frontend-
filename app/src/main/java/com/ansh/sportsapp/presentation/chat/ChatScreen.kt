package com.ansh.sportsapp.presentation.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when messages arrive
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            delay(80)
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    // Show scroll-to-bottom button when user scrolled up
    val showScrollButton by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisible < totalItems - 3
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            ChatTopBar(onBack = { navController.popBackStack() })
        },
        bottomBar = {
            ChatInputBar(
                value = state.messageText,
                onValueChange = viewModel::onMessageChange,
                onSend = { viewModel.sendMessage() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Subtle background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                BackgroundDark,
                                SurfaceDark.copy(alpha = 0.4f),
                                BackgroundDark
                            )
                        )
                    )
            )

            if (state.messages.isEmpty()) {
                ChatEmptyState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 12.dp, end = 12.dp,
                        top = 12.dp, bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item(key = "date_today") {
                        ChatDateSeparator(label = "Today")
                    }

                    items(
                        items = state.messages,
                        key = { it.id }
                    ) { message ->
                        AnimatedVisibility(
                            visible = true,
                            enter = if (message.isFromMe) {
                                fadeIn(tween(180)) +
                                        slideInHorizontally(tween(180)) { it / 4 }
                            } else {
                                fadeIn(tween(180)) +
                                        slideInHorizontally(tween(180)) { -it / 4 }
                            }
                        ) {
                            MessageBubble(message = message)
                        }
                    }
                }
            }

            // Scroll-to-bottom FAB
            AnimatedVisibility(
                visible = showScrollButton,
                enter = fadeIn(tween(200)) + scaleIn(tween(200)),
                exit = fadeOut(tween(200)) + scaleOut(tween(200)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SportGreenContainer)
                        .border(1.dp, SportGreen.copy(alpha = 0.4f), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            coroutineScope.launch {
                                if (state.messages.isNotEmpty()) {
                                    listState.animateScrollToItem(state.messages.size - 1)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Scroll to bottom",
                        tint = SportGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}