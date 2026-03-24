package com.ansh.sportsapp.presentation.auth.signup

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.presentation.auth.login.ErrorBanner
import com.ansh.sportsapp.presentation.auth.login.SportPrimaryButton
import com.ansh.sportsapp.presentation.auth.login.SportTextField
import com.ansh.sportsapp.presentation.navigation.Screen
import com.ansh.sportsapp.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is RegisterUiEvent.RegistrationSuccess -> navController.navigate(Screen.Login.route)
                is RegisterUiEvent.ShowSnackBar -> { /* handled inline */ }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Subtle bg decor — mirrored from login
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-50).dp, y = 80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(TertiaryIndigo.copy(alpha = 0.05f), Color.Transparent)
                    )
                )
                .align(Alignment.TopStart)
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = 60.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(SportGreen.copy(alpha = 0.04f), Color.Transparent)
                    )
                )
                .align(Alignment.BottomEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
        ) {
            // Back button row
            Spacer(Modifier.height(56.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElevatedDark)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            // Header
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 }
            ) {
                RegisterHeader()
            }

            Spacer(Modifier.height(36.dp))

            // Form
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 100)) +
                        slideInVertically(tween(600, delayMillis = 100)) { it / 6 }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceDark)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(20.dp))
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                            Text(
                                text = "Your details",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )

                            SportTextField(
                                value = state.username,
                                onValueChange = { viewModel.onEvent(RegisterEvent.EnteredUsername(it)) },
                                label = "USERNAME",
                                placeholder = "your_gamertag",
                                leadingIcon = Icons.Default.Person,
                                imeAction = ImeAction.Next
                            )

                            SportTextField(
                                value = state.email,
                                onValueChange = { viewModel.onEvent(RegisterEvent.EnteredEmail(it)) },
                                label = "EMAIL",
                                placeholder = "you@example.com",
                                leadingIcon = Icons.Default.Email,
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SportTextField(
                                    value = state.password,
                                    onValueChange = { viewModel.onEvent(RegisterEvent.EnteredPassword(it)) },
                                    label = "PASSWORD",
                                    placeholder = "min. 6 characters",
                                    leadingIcon = Icons.Default.Lock,
                                    isPassword = true,
                                    imeAction = ImeAction.Done,
                                    onImeAction = { viewModel.onEvent(RegisterEvent.Register) }
                                )
                                PasswordStrengthBar(password = state.password)
                            }

                            state.error?.let { ErrorBanner(message = it) }
                        }
                    }

                    // Register CTA
                    SportPrimaryButton(
                        text = "CREATE ACCOUNT",
                        onClick = { viewModel.onEvent(RegisterEvent.Register) },
                        isLoading = state.isLoading
                    )

                    // Login link
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Already on the squad?",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceHint
                            )
                            Text(
                                text = "Sign in",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SportGreen,
                                modifier = Modifier.clickable { navController.popBackStack() }
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}