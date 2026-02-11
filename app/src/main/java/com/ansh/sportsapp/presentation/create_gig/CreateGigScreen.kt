package com.ansh.sportsapp.presentation.create_gig


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
                is CreateGigUiEvent.GigCreated -> {
                    // Go back to home and refresh
                    navController.popBackStack()
                }
                is CreateGigUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create New Gig") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.sport,
                onValueChange = { viewModel.onEvent(CreateGigEvent.EnteredSport(it)) },
                label = { Text("Sport (e.g. Basketball)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.location,
                onValueChange = { viewModel.onEvent(CreateGigEvent.EnteredLocation(it)) },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.date,
                    onValueChange = { viewModel.onEvent(CreateGigEvent.EnteredDate(it)) },
                    label = { Text("Date") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("2025-12-31") }
                )
                OutlinedTextField(
                    value = state.time,
                    onValueChange = { viewModel.onEvent(CreateGigEvent.EnteredTime(it)) },
                    label = { Text("Time") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("18:30") }
                )
            }

            OutlinedTextField(
                value = state.players,
                onValueChange = { viewModel.onEvent(CreateGigEvent.EnteredPlayers(it)) },
                label = { Text("Players Needed") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onEvent(CreateGigEvent.Submit) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Create Gig")
            }
        }
    }
}