package com.ansh.sportsapp.presentation.create_gig

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import java.util.Calendar
import java.util.TimeZone
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ansh.sportsapp.data.remote.dto.nominatim.NominatimResultDto
import com.ansh.sportsapp.ui.theme.*
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

private const val MAP_STYLE_CREATE = "https://tiles.openfreemap.org/styles/liberty"

// ─── Top bar ─────────────────────────────────────────────────────────────────

@Composable
fun CreateGigTopBar(onBack: () -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElevatedDark)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                    .clickable { onBack() }
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = "CREATE GIG",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = SportGreen,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Set up your game",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceHint,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, SportGreen.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )
    }
}

// ─── Step section header ──────────────────────────────────────────────────────

@Composable
fun StepSectionHeader(step: Int, title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Step number badge
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SportGreenContainer)
                .border(1.dp, SportGreen.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.toString(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = SportGreen
            )
        }
        Icon(icon, contentDescription = null, tint = OnSurfaceHint, modifier = Modifier.size(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = OnSurface
        )
    }
}

// ─── Sport chip selector ──────────────────────────────────────────────────────

@Composable
fun SportChipSelector(
    sports: List<Pair<Long, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        StepSectionHeader(1, "Sport", Icons.Default.SportsSoccer)

        // 2-column grid
        val rows = sports.chunked(3)
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (_, sportName) ->
                    val isSelected = selected.equals(sportName, ignoreCase = true)
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) SportGreenContainer else ElevatedDark,
                        animationSpec = tween(180),
                        label = "sportBg"
                    )
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) SportGreen.copy(alpha = 0.6f) else OutlineVariant,
                        animationSpec = tween(180),
                        label = "sportBorder"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) SportGreen else OnSurfaceVariant,
                        animationSpec = tween(180),
                        label = "sportText"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgColor)
                            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                            .clickable { onSelect(sportName) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sportName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = textColor
                        )
                    }
                }
                // Fill remaining cells if row is not full
                repeat(3 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// ─── Section card wrapper ─────────────────────────────────────────────────────

@Composable
fun GigSectionCard(content: @Composable () -> Unit) {
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

// ─── Location search field ────────────────────────────────────────────────────

@Composable
fun LocationSearchField(
    query: String,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit
) {
    var tfValue by remember { mutableStateOf(TextFieldValue(query, TextRange(query.length))) }

    // When query changes externally (suggestion selected / reverse geocode), put cursor at end
    LaunchedEffect(query) {
        if (tfValue.text != query) {
            tfValue = TextFieldValue(query, TextRange(query.length))
        }
    }

    OutlinedTextField(
        value = tfValue,
        onValueChange = { new ->
            tfValue = new
            onQueryChange(new.text)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search for a venue or address", style = MaterialTheme.typography.bodySmall) },
        leadingIcon = {
            if (isSearching) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = SportGreen)
            } else {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        },
        trailingIcon = {
            if (tfValue.text.isNotBlank()) {
                IconButton(onClick = {
                    tfValue = TextFieldValue("")
                    onQueryChange("")
                }) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        textStyle = MaterialTheme.typography.bodySmall,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = OutlineVariant,
            focusedBorderColor = SportGreen,
            unfocusedContainerColor = ElevatedDark,
            focusedContainerColor = ElevatedDark,
            cursorColor = SportGreen,
            unfocusedTextColor = OnSurface,
            focusedTextColor = OnSurface,
            unfocusedPlaceholderColor = OnSurfaceHint,
            focusedPlaceholderColor = OnSurfaceHint
        )
    )
}

// ─── Location suggestions list ────────────────────────────────────────────────

@Composable
fun LocationSuggestionList(
    suggestions: List<NominatimResultDto>,
    onSelect: (NominatimResultDto) -> Unit
) {
    if (suggestions.isEmpty()) return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
    ) {
        Column {
            suggestions.forEachIndexed { index, result ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(result) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = SportGreen)
                    Text(text = result.displayName, style = MaterialTheme.typography.bodySmall, color = OnSurface, maxLines = 2)
                }
                if (index < suggestions.lastIndex) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(OutlineVariant))
                }
            }
        }
    }
}

// ─── Location map picker ──────────────────────────────────────────────────────

@Composable
fun LocationMapPicker(
    lat: Double,
    lng: Double,
    onPinMoved: (Double, Double) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    LocationPreviewCard(lat = lat, lng = lng, onEditClick = { showDialog = true })

    if (showDialog) {
        LocationMapDialog(
            initialLat = lat,
            initialLng = lng,
            onConfirm = { finalLat, finalLng ->
                showDialog = false
                onPinMoved(finalLat, finalLng)
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun LocationPreviewCard(lat: Double, lng: Double, onEditClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ElevatedDark)
            .border(1.dp, SportGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SportGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = SportGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pin placed",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = OnSurface
                )
                Text(
                    text = "%.5f, %.5f".format(lat, lng),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceHint
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SportGreenContainer)
                    .border(1.dp, SportGreen.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .clickable { onEditClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Edit on map",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = SportGreen
                )
            }
        }
    }
}

@Composable
private fun LocationMapDialog(
    initialLat: Double,
    initialLng: Double,
    onConfirm: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var pinLat by remember { mutableStateOf(initialLat) }
    var pinLng by remember { mutableStateOf(initialLng) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var markerRef by remember { mutableStateOf<Marker?>(null) }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView?.onStart()
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                Lifecycle.Event.ON_STOP -> mapView?.onStop()
                Lifecycle.Event.ON_DESTROY -> { mapView?.onDestroy(); mapView = null }
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView?.onDestroy()
            mapView = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Full-screen interactive map
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).also { mv ->
                        mapView = mv
                        mv.onCreate(null)
                        mv.getMapAsync { map ->
                            map.setStyle(Style.Builder().fromUri(MAP_STYLE_CREATE)) {
                                map.cameraPosition = CameraPosition.Builder()
                                    .target(LatLng(initialLat, initialLng))
                                    .zoom(14.0)
                                    .build()
                                markerRef = map.addMarker(MarkerOptions().position(LatLng(initialLat, initialLng)))
                                map.addOnMapClickListener { tapped ->
                                    markerRef?.remove()
                                    markerRef = map.addMarker(MarkerOptions().position(tapped))
                                    pinLat = tapped.latitude
                                    pinLng = tapped.longitude
                                    true
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
                Text(
                    text = "Adjust Pin",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Tap hint pill
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-48).dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Tap anywhere to move pin",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            // Bottom action bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "© OpenStreetMap contributors",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color.White.copy(alpha = 0.6f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("Cancel", color = Color.White.copy(alpha = 0.8f))
                    }
                    Button(
                        onClick = { onConfirm(pinLat, pinLng) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SportGreen)
                    ) {
                        Text("Confirm", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─── Date picker field ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigDatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    val initialMillis = remember {
        if (value.isBlank()) {
            Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
        } else {
            try {
                val parts = value.split("-")
                Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), 12, 0, 0)
                }.timeInMillis
            } catch (e: Exception) {
                Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
            }
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "DATE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = OnSurfaceHint
        )
        GigPickerField(
            value = value,
            placeholder = "YYYY-MM-DD",
            leadingIcon = Icons.Default.DateRange,
            onClick = { showPicker = true }
        )
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = millis
                            }
                            onDateSelected(
                                String.format(
                                    "%04d-%02d-%02d",
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH) + 1,
                                    cal.get(Calendar.DAY_OF_MONTH)
                                )
                            )
                        }
                    }
                ) { Text("OK", color = SportGreen, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel", color = OnSurfaceHint)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = SurfaceVariantDark)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = SurfaceVariantDark,
                    titleContentColor = OnSurfaceHint,
                    headlineContentColor = SportGreen,
                    weekdayContentColor = OnSurfaceVariant,
                    navigationContentColor = OnSurface,
                    yearContentColor = OnSurface,
                    currentYearContentColor = SportGreen,
                    selectedYearContainerColor = SportGreen,
                    selectedYearContentColor = OnSurface,
                    dayContentColor = OnSurface,
                    disabledDayContentColor = OnSurfaceDisabled,
                    selectedDayContainerColor = SportGreen,
                    selectedDayContentColor = OnSurface,
                    todayContentColor = SportGreen,
                    todayDateBorderColor = SportGreen
                )
            )
        }
    }
}

// ─── Time picker field ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigTimePickerField(
    value: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    val (initHour, initMinute) = remember {
        if (value.isBlank()) 18 to 0
        else {
            try {
                val parts = value.split(":")
                parts[0].toInt() to parts[1].toInt()
            } catch (e: Exception) {
                18 to 0
            }
        }
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initHour,
        initialMinute = initMinute,
        is24Hour = true
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "TIME",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = OnSurfaceHint
        )
        GigPickerField(
            value = value,
            placeholder = "HH:MM",
            leadingIcon = Icons.Default.Schedule,
            onClick = { showPicker = true }
        )
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            containerColor = SurfaceVariantDark,
            title = {
                Text(
                    text = "Pick a time (24h)",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface
                )
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = ElevatedDark,
                        clockDialSelectedContentColor = OnSurface,
                        clockDialUnselectedContentColor = OnSurfaceVariant,
                        selectorColor = SportGreen,
                        containerColor = SurfaceVariantDark,
                        timeSelectorSelectedContainerColor = SportGreenContainer,
                        timeSelectorUnselectedContainerColor = ElevatedDark,
                        timeSelectorSelectedContentColor = SportGreen,
                        timeSelectorUnselectedContentColor = OnSurfaceVariant
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPicker = false
                    onTimeSelected(String.format("%02d:%02d", timePickerState.hour, timePickerState.minute))
                }) { Text("OK", color = SportGreen, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel", color = OnSurfaceHint)
                }
            }
        )
    }
}

// ─── Picker field (shared internal display) ───────────────────────────────────

@Composable
private fun GigPickerField(
    value: String,
    placeholder: String,
    leadingIcon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ElevatedDark)
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(17.dp), tint = OnSurfaceHint)
            Text(
                text = value.ifBlank { placeholder },
                style = MaterialTheme.typography.bodyMedium,
                color = if (value.isBlank()) OnSurfaceDisabled else OnSurface
            )
        }
    }
}

// ─── Gig form text field ──────────────────────────────────────────────────────

@Composable
fun GigFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = OnSurfaceHint
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, style = MaterialTheme.typography.bodySmall, color = OnSurfaceDisabled)
            },
            leadingIcon = {
                Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = OutlineVariant,
                focusedBorderColor = SportGreen,
                unfocusedContainerColor = ElevatedDark,
                focusedContainerColor = ElevatedDark,
                cursorColor = SportGreen,
                unfocusedTextColor = OnSurface,
                focusedTextColor = OnSurface,
                unfocusedPlaceholderColor = OnSurfaceDisabled,
                focusedPlaceholderColor = OnSurfaceDisabled,
                unfocusedLeadingIconColor = OnSurfaceHint,
                focusedLeadingIconColor = SportGreen
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}