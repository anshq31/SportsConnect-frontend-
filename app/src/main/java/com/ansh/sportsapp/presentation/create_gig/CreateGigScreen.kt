package com.ansh.sportsapp.presentation.create_gig

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
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
                .windowInsetsPadding(WindowInsets.ime)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Step 1: Sport ─────────────────────────────────────────
            GigSectionCard {
                SportChipSelector(
                    sports = viewModel.availableSports,
                    selected = state.sport,
                    onSelect = { viewModel.onEvent(CreateGigEvent.EnteredSport(it)) }
                )
            }

            // ── Step 2: Location ──────────────────────────────────────
            GigSectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StepSectionHeader(2, "Location", Icons.Default.LocationOn)

                    LocationSearchField(
                        query = state.locationQuery,
                        isSearching = state.isSearchingLocation,
                        onQueryChange = { viewModel.onEvent(CreateGigEvent.LocationQueryChanged(it)) }
                    )

                    LocationSuggestionList(
                        suggestions = state.locationSuggestions,
                        onSelect = { viewModel.onEvent(CreateGigEvent.SuggestionSelected(it)) }
                    )

                    // Capture as local vals to fix smart cast on delegated property
                    val selectedLat = state.selectedLat
                    val selectedLng = state.selectedLng

                    if (selectedLat != null && selectedLng != null) {
                        LocationMapPicker(
                            lat = selectedLat,
                            lng = selectedLng,
                            onPinMoved = { lat, lng ->
                                viewModel.onEvent(CreateGigEvent.MarkerDragged(lat, lng))
                            }
                        )

                        if (state.locationDisplay.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = OnSurfaceHint
                                )
                                Text(
                                    text = state.locationDisplay,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceHint,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            // ── Step 3: Date & Time ───────────────────────────────────
            GigSectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StepSectionHeader(3, "Schedule", Icons.Default.Event)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GigDatePickerField(
                            value = state.date,
                            onDateSelected = { viewModel.onEvent(CreateGigEvent.EnteredDate(it)) },
                            modifier = Modifier.weight(1f)
                        )
                        GigTimePickerField(
                            value = state.time,
                            onTimeSelected = { viewModel.onEvent(CreateGigEvent.EnteredTime(it)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── Step 4: Players ───────────────────────────────────────
            GigSectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StepSectionHeader(4, "Squad Size", Icons.Default.Group)

                    val quickValues = listOf(2, 4, 6, 8, 10, 12)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        quickValues.forEach { n ->
                            val isSelected = state.players == n.toString()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SportGreenContainer else ElevatedDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) SportGreen.copy(alpha = 0.5f) else OutlineVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.onEvent(CreateGigEvent.EnteredPlayers(n.toString())) }
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
                        state.selectedLat != null &&
                        state.date.isNotBlank() &&
                        state.time.isNotBlank() &&
                        state.players.isNotBlank()
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}
