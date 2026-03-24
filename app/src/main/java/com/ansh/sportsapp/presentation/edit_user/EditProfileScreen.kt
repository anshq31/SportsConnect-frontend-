package com.ansh.sportsapp.presentation.edit_user

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
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ansh.sportsapp.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is EditProfileUiEvent.SaveSuccess -> {
                    snackbarHostState.showSnackbar("Profile updated!")
                    navController.popBackStack()
                }
                is EditProfileUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EditProfileTopBar(
                username = null, // Will be loaded
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SportGreen,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            "Loading profile...",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceHint
                        )
                    }
                }
            }

            state.error != null && !state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ErrorContainer)
                                .border(1.dp, ErrorRed.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline, null,
                                tint = ErrorRed, modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            state.error ?: "Something went wrong",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceHint
                        )
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // ── Experience section ────────────────────────────
                    EditSectionCard(
                        title = "Experience",
                        icon = Icons.Default.EmojiEvents
                    ) {
                        Text(
                            text = "Describe your sports background",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceHint
                        )

                        OutlinedTextField(
                            value = state.experience,
                            onValueChange = { viewModel.onExperienceChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "e.g. Played basketball for 5 years, love weekend football...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceDisabled
                                )
                            },
                            minLines = 4,
                            maxLines = 8,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = SportGreen,
                                unfocusedContainerColor = ElevatedDark,
                                focusedContainerColor = ElevatedDark,
                                cursorColor = SportGreen,
                                unfocusedTextColor = OnSurface,
                                focusedTextColor = OnSurface,
                                unfocusedPlaceholderColor = OnSurfaceDisabled,
                                focusedPlaceholderColor = OnSurfaceDisabled
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // ── Skills section ────────────────────────────────
                    EditSectionCard(
                        title = "Skills",
                        icon = Icons.Default.SportsSoccer
                    ) {
                        Text(
                            text = "Select the sports you play",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceHint
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            viewModel.availableSkills.forEach { (id, name) ->
                                SelectableSkillChip(
                                    skill = name,
                                    isSelected = state.selectedSkillIds.contains(id),
                                    onClick = { viewModel.onSkillToggle(id) }
                                )
                            }
                        }

                        // Selected count
                        AnimatedVisibility(visible = state.selectedSkillIds.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(TertiaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${state.selectedSkillIds.size} selected",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TertiaryIndigo,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // ── Save button ───────────────────────────────────
                    SaveButton(
                        isSaving = state.isSaving,
                        onClick = { viewModel.saveProfile() }
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}