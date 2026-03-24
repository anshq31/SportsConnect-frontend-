package com.ansh.sportsapp.presentation.auth.login


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import com.ansh.sportsapp.ui.theme.*

// ─── Brand Header ─────────────────────────────────────────────────────────────

@Composable
fun LoginBrandHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Decorative accent bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SportGreen)
            )
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SportGreen.copy(alpha = 0.4f))
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SportGreen.copy(alpha = 0.2f))
            )
        }

        Text(
            text = "SPORTS",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                fontSize = 42.sp
            ),
            color = OnSurface
        )

        Text(
            text = "CONNECT",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                fontSize = 42.sp
            ),
            color = SportGreen.copy(alpha = glowAlpha)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Find your game. Build your squad.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceHint
        )
    }
}

// ─── Login Field ──────────────────────────────────────────────────────────────

@Composable
fun SportTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isFocused = remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused.value) SportGreen else OutlineVariant,
        animationSpec = tween(200),
        label = "border"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isFocused.value) SportGreen else OnSurfaceHint,
        animationSpec = tween(200),
        label = "label"
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = labelColor
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ElevatedDark)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .onFocusChanged { isFocused.value = it.isFocused },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                cursorBrush = SolidColor(SportGreen),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onImeAction() },
                    onDone = { onImeAction() }
                ),
                visualTransformation = if (isPassword && !passwordVisible)
                    PasswordVisualTransformation() else VisualTransformation.None,
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (isFocused.value) SportGreen else OnSurfaceHint
                        )
                        Box(Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceDisabled
                                )
                            }
                            innerTextField()
                        }
                        if (isPassword) {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = OnSurfaceHint
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

// ─── Primary CTA Button ───────────────────────────────────────────────────────

@Composable
fun SportPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = if (isLoading) 0.97f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .scale(scale),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SportGreen,
            contentColor = Color(0xFF0A0C0F),
            disabledContainerColor = SportGreen.copy(alpha = 0.4f),
            disabledContentColor = Color(0xFF0A0C0F).copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
            label = "btnContent"
        ) { loading ->
            if (loading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color(0xFF0A0C0F),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Please wait...",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

// ─── Error Banner ─────────────────────────────────────────────────────────────

@Composable
fun ErrorBanner(message: String) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ErrorContainer)
                .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = ErrorRed
            )
        }
    }
}

// ─── Decorative Background Grid ───────────────────────────────────────────────

@Composable
fun SportBackgroundDecor(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // Top right decorative circle
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 120.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            SportGreen.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.TopEnd)
        )

        // Bottom left decorative circle
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = (-60).dp, y = 40.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            TertiaryIndigo.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.BottomStart)
        )
    }
}