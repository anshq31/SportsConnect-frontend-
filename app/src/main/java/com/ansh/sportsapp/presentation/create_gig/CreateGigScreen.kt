package com.ansh.sportsapp.presentation.create_gig

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.presentation.auth.login.SportPrimaryButton
import com.ansh.sportsapp.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGigScreen(
    navController: NavController,
    viewModel: CreateGigViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CreateGigUiEvent.GigCreated -> navController.popBackStack()
                is CreateGigUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CreateGigTopBar(onBack = { navController.popBackStack() })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Step 1: Sport ─────────────────────────────────────────
            SectionCard {
                SportChipSelector(
                    sports = viewModel.availableSports,
                    selected = state.sport,
                    onSelect = { viewModel.onEvent(CreateGigEvent.EnteredSport(it)) }
                )
            }

            // ── Step 2: Location ──────────────────────────────────────
            SectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StepSectionHeader(2, "Location", Icons.Default.LocationOn)
                    GigFormField(
                        value = state.location,
                        onValueChange = { viewModel.onEvent(CreateGigEvent.EnteredLocation(it)) },
                        label = "VENUE",
                        placeholder = "e.g. Central Park, Court 3",
                        icon = Icons.Default.LocationOn
                    )
                }
            }

            // ── Step 3: Date & Time ───────────────────────────────────
            SectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StepSectionHeader(3, "Schedule", Icons.Default.Event)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GigFormField(
                            value = state.date,
                            onValueChange = { viewModel.onEvent(CreateGigEvent.EnteredDate(it)) },
                            label = "DATE",
                            placeholder = "2026-12-31",
                            icon = Icons.Default.DateRange,
                            modifier = Modifier.weight(1f)
                        )
                        GigFormField(
                            value = state.time,
                            onValueChange = { viewModel.onEvent(CreateGigEvent.EnteredTime(it)) },
                            label = "TIME",
                            placeholder = "18:30",
                            icon = Icons.Default.Schedule,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── Step 4: Players ───────────────────────────────────────
            SectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StepSectionHeader(4, "Squad Size", Icons.Default.Group)

                    // Quick select buttons
                    val quickValues = listOf(2, 4, 6, 8, 10, 12)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        quickValues.forEach { n ->
                            val isSelected = state.players == n.toString()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) SportGreenContainer else ElevatedDark
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) SportGreen.copy(alpha = 0.5f) else OutlineVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.onEvent(CreateGigEvent.EnteredPlayers(n.toString()))
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$n",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) SportGreen else OnSurfaceHint
                                )
                            }
                        }
                    }

                    GigFormField(
                        value = state.players,
                        onValueChange = { viewModel.onEvent(CreateGigEvent.EnteredPlayers(it)) },
                        label = "OR ENTER CUSTOM NUMBER",
                        placeholder = "e.g. 5",
                        icon = Icons.Default.Person,
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Submit ────────────────────────────────────────────────
            SportPrimaryButton(
                text = "CREATE GIG",
                onClick = { viewModel.onEvent(CreateGigEvent.Submit) },
                isLoading = state.isLoading,
                enabled = state.sport.isNotBlank() &&
                        state.location.isNotBlank() &&
                        state.date.isNotBlank() &&
                        state.time.isNotBlank() &&
                        state.players.isNotBlank()
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─── Section card wrapper ─────────────────────────────────────────────────────

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        content()
    }
}