package com.ansh.sportsapp.presentation.auth.login

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.presentation.navigation.Screen
import com.ansh.sportsapp.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is LoginUiEvent.NavigateHome -> {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
                is LoginUiEvent.ShowSnackBar -> { /* handled by error state */ }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Decorative background
        SportBackgroundDecor(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.ime)
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(72.dp))

            // Brand header - staggered entrance
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
            ) {
                LoginBrandHeader()
            }

            Spacer(Modifier.height(48.dp))

            // Form card
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, delayMillis = 150)) +
                        slideInVertically(tween(700, delayMillis = 150)) { it / 6 }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                    // Form fields card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceDark)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(20.dp))
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                            // Section label
                            Text(
                                text = "Welcome back",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = OnSurface
                            )
                            Text(
                                text = "Sign in to continue to your squad",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceHint
                            )

                            Spacer(Modifier.height(4.dp))

                            SportTextField(
                                value = state.username,
                                onValueChange = { viewModel.onEvent(LoginEvent.EnteredUsername(it)) },
                                label = "USERNAME",
                                placeholder = "your_username",
                                leadingIcon = Icons.Default.Person,
                                imeAction = ImeAction.Next
                            )

                            SportTextField(
                                value = state.password,
                                onValueChange = { viewModel.onEvent(LoginEvent.EnteredPassword(it)) },
                                label = "PASSWORD",
                                placeholder = "••••••••",
                                leadingIcon = Icons.Default.Lock,
                                isPassword = true,
                                imeAction = ImeAction.Done,
                                onImeAction = { viewModel.onEvent(LoginEvent.Login) }
                            )

                            // Error banner
                            state.error?.let { error ->
                                ErrorBanner(message = error)
                            }
                        }
                    }

                    // CTA
                    SportPrimaryButton(
                        text = "SIGN IN",
                        onClick = { viewModel.onEvent(LoginEvent.Login) },
                        isLoading = state.isLoading
                    )

                    // Register link
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "New to the game?",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceHint
                            )
                            Text(
                                text = "Register",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SportGreen,
                                modifier = Modifier.clickable {
                                    navController.navigate(Screen.Register.route)
                                }
                            )
                        }

                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "By continuing, you agree to our",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceHint
                            )
                            Text(
                                text = "Privacy Policy",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SportGreen,
                                modifier = Modifier.clickable {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.privacypolicies.com/live/ca3c4faa-12c6-4cc5-a07e-cf9ebe2d056c")
                                    )
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}