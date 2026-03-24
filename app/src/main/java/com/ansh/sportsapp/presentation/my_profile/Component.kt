package com.ansh.sportsapp.presentation.my_profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.ansh.sportsapp.domain.model.Review
import com.ansh.sportsapp.domain.model.UserProfile
import com.ansh.sportsapp.presentation.gig_detail.SectionHeader
import com.ansh.sportsapp.ui.theme.*

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    username: String?,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = "PROFILE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = SportGreen
                )
                Text(
                    text = username?.let { "@$it" } ?: "Your account",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = OnSurface
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Edit button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElevatedDark)
                        .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                // Logout button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorContainer)
                        .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                ) {
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(SportGreen.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )
    }
}

// ─── Profile Content ─────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileContent(
    profile: UserProfile,
    reviews: List<Review>,
    isReviewLoading: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero card
        item { ProfileHeroCard(profile = profile) }

        // Experience
        if (profile.experience.isNotBlank()) {
            item {
                ProfileSectionCard(title = "Experience") {
                    Text(
                        text = profile.experience,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Skills
        if (profile.skills.isNotEmpty()) {
            item {
                ProfileSectionCard(title = "Skills") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        profile.skills.forEach { skill ->
                            SkillChip(skill = skill)
                        }
                    }
                }
            }
        }

        // Reviews header
        item(key = "reviews_header") {
            SectionHeader(title = "Reviews", count = reviews.size)
        }

        // Reviews body
        when {
            isReviewLoading -> {
                item(key = "reviews_loading") {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = SportGreen,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            reviews.isEmpty() -> {
                item(key = "reviews_empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceVariantDark)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = OnSurfaceHint,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "No reviews yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceHint
                            )
                        }
                    }
                }
            }
            else -> {
                items(items = reviews, key = { "review_${it.id}" }) { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }
}

// ─── Hero card ────────────────────────────────────────────────────────────────

@Composable
fun ProfileHeroCard(profile: UserProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceVariantDark)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(SportGreen.copy(alpha = 0.25f), OutlineVariant)
                ),
                RoundedCornerShape(20.dp)
            )
    ) {
        // Top gradient stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(SportGreen.copy(alpha = 0.9f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(SportGreenContainer, ElevatedDark)
                        )
                    )
                    .border(2.dp, SportGreen.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.username.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = SportGreen
                )
            }

            Text(
                text = profile.username,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = OnSurface
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RatingBar(rating = profile.overallRating)
                Text(
                    text = "%.1f / 5.0".format(profile.overallRating),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceHint
                )
            }
        }
    }
}

// ─── Profile Section Card ─────────────────────────────────────────────────────

@Composable
fun ProfileSectionCard(title: String, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Section title with left accent bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(SportGreen)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = OnSurface
                )
            }
            content()
        }
    }
}

// ─── Skill Chip ───────────────────────────────────────────────────────────────

@Composable
fun SkillChip(skill: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TertiaryContainer)
            .border(1.dp, TertiaryIndigo.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = skill,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = TertiaryIndigo
        )
    }
}

// ─── Review Card ─────────────────────────────────────────────────────────────

@Composable
fun ReviewCard(review: Review) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ElevatedDark)
                            .border(1.dp, OutlineVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.reviewerUsername.first().uppercaseChar().toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = OnSurfaceVariant
                        )
                    }
                    Text(
                        text = "@${review.reviewerUsername}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                        color = OnSurface
                    )
                }

                RatingBar(rating = review.rating.toDouble(), starSize = 14)
            }

            if (review.comment.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElevatedDark)
                        .padding(10.dp)
                ) {
                    Text(
                        text = review.comment,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// ─── Rating Bar ───────────────────────────────────────────────────────────────

@Composable
fun RatingBar(
    rating: Double,
    starSize: Int = 20,
    highlightColor: Color = WarningAmber
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..5) {
            Icon(
                imageVector = when {
                    rating >= i -> Icons.Default.Star
                    rating >= i - 0.5 -> Icons.Default.StarHalf
                    else -> Icons.Default.StarBorder
                },
                contentDescription = null,
                modifier = Modifier.size(starSize.dp),
                tint = if (rating >= i - 0.5) highlightColor else OnSurfaceDisabled
            )
        }
    }
}

// ─── States ───────────────────────────────────────────────────────────────────

@Composable
fun ProfileLoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator(
                color = SportGreen,
                strokeWidth = 2.dp,
                modifier = Modifier.size(28.dp)
            )
            Text("Loading profile...", style = MaterialTheme.typography.bodySmall, color = OnSurfaceHint)
        }
    }
}

@Composable
fun ProfileErrorState(message: String, onRetry: () -> Unit) {
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
                Icon(Icons.Default.ErrorOutline, null, Modifier.size(30.dp), tint = ErrorRed)
            }
            Text(message, style = MaterialTheme.typography.bodySmall, color = OnSurfaceHint)
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