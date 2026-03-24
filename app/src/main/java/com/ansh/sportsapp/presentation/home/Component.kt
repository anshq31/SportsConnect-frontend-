package com.ansh.sportsapp.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.ui.theme.*

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
fun HomeTopBar(
    isScrolled: Boolean,
    sportQuery: String,
    locationQuery: String,
    onSportChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    val elevationAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f,
        animationSpec = tween(200),
        label = "elevation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        SurfaceDark,
                        SurfaceDark.copy(alpha = if (isScrolled) 0.97f else 1f)
                    )
                )
            )
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DISCOVER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = SportGreen
                )
                Text(
                    text = "Active Games",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = OnSurface
                )
            }

            // Live pill
            LiveIndicator()
        }

        // Search bar
        HomeSearchBar(
            sportQuery = sportQuery,
            locationQuery = locationQuery,
            onSportChange = onSportChange,
            onLocationChange = onLocationChange,
            onClearFilters = onClearFilters
        )

        // Bottom accent line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(SportGreen.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
private fun LiveIndicator() {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseAnim.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SportGreenContainer)
            .border(1.dp, SportGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(SportGreen.copy(alpha = pulseAlpha))
        )
        Text(
            text = "LIVE",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            color = SportGreen
        )
    }
}

// ─── Search Bar ───────────────────────────────────────────────────────────────

@Composable
fun HomeSearchBar(
    sportQuery: String,
    locationQuery: String,
    onSportChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasFilter = sportQuery.isNotBlank() || locationQuery.isNotBlank()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = OutlineVariant,
        focusedBorderColor = SportGreen,
        unfocusedContainerColor = ElevatedDark,
        focusedContainerColor = ElevatedDark,
        cursorColor = SportGreen,
        unfocusedTextColor = OnSurface,
        focusedTextColor = OnSurface,
        unfocusedPlaceholderColor = OnSurfaceHint,
        focusedPlaceholderColor = OnSurfaceHint,
        unfocusedLeadingIconColor = OnSurfaceHint,
        focusedLeadingIconColor = SportGreen,
        unfocusedTrailingIconColor = OnSurfaceHint,
        focusedTrailingIconColor = OnSurfaceHint
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = sportQuery,
                onValueChange = onSportChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Sport", style = MaterialTheme.typography.bodySmall)
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.SportsSoccer,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(sportQuery.isNotBlank()) {
                        IconButton(
                            onClick = { onSportChange("") },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                colors = fieldColors
            )

            OutlinedTextField(
                value = locationQuery,
                onValueChange = onLocationChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Location", style = MaterialTheme.typography.bodySmall)
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(locationQuery.isNotBlank()) {
                        IconButton(
                            onClick = { onLocationChange("") },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                colors = fieldColors
            )
        }

        AnimatedVisibility(
            visible = hasFilter,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (sportQuery.isNotBlank()) {
                    ActiveFilterChip(label = sportQuery, onRemove = { onSportChange("") })
                }
                if (locationQuery.isNotBlank()) {
                    ActiveFilterChip(label = locationQuery, onRemove = { onLocationChange("") })
                }
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = onClearFilters,
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text(
                        "Clear all",
                        style = MaterialTheme.typography.labelSmall,
                        color = ErrorRed
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveFilterChip(label: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SportGreenContainer)
            .border(1.dp, SportGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SportGreen
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(16.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = SportGreen
            )
        }
    }
}

// ─── Gig Card ─────────────────────────────────────────────────────────────────

@Composable
fun GigCard(
    gig: Gig,
    onItemClick: (Gig) -> Unit
) {
    val statusColor = gigStatusColor(gig.status)
    val statusContainer = gigStatusContainer(gig.status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(statusColor.copy(alpha = 0.25f), OutlineVariant)
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = { onItemClick(gig) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Thin status stripe at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(statusColor.copy(alpha = 0.8f), Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Sport + status row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sport pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SportGreenContainer)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = gig.sport.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = SportGreen
                        )
                    }

                    // Status chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(statusContainer)
                            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = gig.status.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = statusColor
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Info rows
                GigInfoItem(icon = Icons.Default.LocationOn, text = gig.location, tint = OnSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                GigInfoItem(
                    icon = Icons.Default.Event,
                    text = gig.dateTime.replace("T", " · "),
                    tint = OnSurfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(OutlineVariant)
                )

                Spacer(Modifier.height(12.dp))

                // Bottom row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Host avatar + name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(SportGreenContainer)
                                .border(1.dp, SportGreen.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = gig.gigMasterUsername.first().uppercaseChar().toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = SportGreen
                            )
                        }
                        Text(
                            text = "@${gig.gigMasterUsername}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceHint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Players badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElevatedDark)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp),
                            tint = OnSurfaceHint
                        )
                        Text(
                            text = "${gig.playersNeeded} needed",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GigInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color = SportGreen
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = tint
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─── Count badge ──────────────────────────────────────────────────────────────

@Composable
fun GigCountBadge(count: Int) {
    Row(
        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(SportGreen)
        )
        Text(
            text = "$count gig${if (count != 1) "s" else ""} available",
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceHint
        )
    }
}

// ─── Empty / Error / Loading ──────────────────────────────────────────────────

@Composable
fun HomeLoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                color = SportGreen,
                strokeWidth = 2.dp,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Finding games...",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceHint
            )
        }
    }
}

@Composable
fun HomeEmptyState(hasFilters: Boolean) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ElevatedDark)
                    .border(1.dp, OutlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SportsSoccer,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = OnSurfaceHint
                )
            }
            Text(
                text = if (hasFilters) "No matches found" else "No active games",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                text = if (hasFilters) "Try adjusting your filters" else "Check back soon for new games",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceHint
            )
        }
    }
}

@Composable
fun HomeErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ErrorContainer)
                    .border(1.dp, ErrorRed.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = ErrorRed
                )
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceHint
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(SportGreenContainer)
                    .border(1.dp, SportGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            ) {
                TextButton(onClick = onRetry) {
                    Text("Retry", color = SportGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}