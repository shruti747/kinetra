package com.kinetra.app.ui.tracking

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.kinetra.app.tracking.model.ActivityType
import com.kinetra.app.tracking.model.GpsPoint
import com.kinetra.app.tracking.model.LiveMetrics
import com.kinetra.app.tracking.model.TrackingState
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraScreenHeader
import com.kinetra.app.ui.theme.*
import com.kinetra.app.wearable.AccelerometerReading
import com.kinetra.app.wearable.GyroscopeReading
import com.kinetra.app.wearable.MotionReading

@Composable
fun LiveTrackingScreen(
    viewModel: LiveTrackingViewModel,
    onBack: () -> Unit,
    onDevicesClick: () -> Unit
) {
    val context = LocalContext.current

    val selectedActivity by viewModel.selectedActivity.collectAsState()
    val trackingState by viewModel.trackingState.collectAsState()
    val metrics by viewModel.liveMetrics.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val isPhoneFallback by viewModel.isUsingPhoneFallback.collectAsState()
    val liveMotion by viewModel.liveMotion.collectAsState()

    var showStopDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            if (trackingState == TrackingState.FINISHED) {
                viewModel.resetTracking()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val fineGranted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.startTracking(context)
        }
    }

    LiveTrackingContent(
        selectedActivity = selectedActivity,
        trackingState = trackingState,
        metrics = metrics,
        routePoints = routePoints,
        isPhoneFallback = isPhoneFallback,
        liveMotion = liveMotion,
        onBack = {
            if (trackingState == TrackingState.FINISHED) {
                viewModel.resetTracking()
            }
            onBack()
        },
        onDevicesClick = onDevicesClick,
        onSelectActivity = { viewModel.setActivity(it) },
        onStartClick = {
            val finePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            val coarsePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (finePerm == PackageManager.PERMISSION_GRANTED || coarsePerm == PackageManager.PERMISSION_GRANTED) {
                viewModel.startTracking(context)
            } else {
                val perms = mutableListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    perms.add(Manifest.permission.POST_NOTIFICATIONS)
                }
                permissionLauncher.launch(perms.toTypedArray())
            }
        },
        onPauseClick = { viewModel.pauseTracking(context) },
        onResumeClick = { viewModel.resumeTracking(context) },
        onFinishRequest = { showStopDialog = true },
        onResetClick = { viewModel.resetTracking() },
        onDoneClick = {
            viewModel.resetTracking()
            onBack()
        }
    )

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = {
                Text(
                    text = "Finish Workout?",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you ready to complete and save this workout session to your history?",
                    color = TextGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showStopDialog = false
                        viewModel.stopTracking(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Complete & Save", color = OnAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) {
                    Text("Continue", color = TextGray, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = CardBackgroundElevated
        )
    }
}

@Composable
internal fun LiveTrackingContent(
    selectedActivity: ActivityType,
    trackingState: TrackingState,
    metrics: LiveMetrics,
    routePoints: List<GpsPoint>,
    isPhoneFallback: Boolean,
    liveMotion: MotionReading?,
    onBack: () -> Unit,
    onDevicesClick: () -> Unit,
    onSelectActivity: (ActivityType) -> Unit,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onFinishRequest: () -> Unit,
    onResetClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar
            KinetraScreenHeader(
                title = "Live Tracking",
                subtitle = "KINETRA TELEMETRY",
                onBack = onBack,
                trailingContent = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (isPhoneFallback) ButtonBackground else AccentTintSurface)
                            .border(
                                width = 1.dp,
                                color = if (isPhoneFallback) ButtonBorder else BrandPrimary.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable { onDevicesClick() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isPhoneFallback) AthleticAmber else AthleticGreen)
                            )
                            Text(
                                text = if (isPhoneFallback) "Phone Sensors" else "Watch Connected",
                                color = if (isPhoneFallback) TextSecondary else BrandAccentText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            )

            // Activity Mode Selector (Walking, Running, Cycling, General Workout)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityType.values().forEach { activity ->
                    val isSelected = selectedActivity == activity
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) BrandPrimary else ButtonBackground)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) BrandPrimary else ButtonBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = trackingState == TrackingState.IDLE) {
                                onSelectActivity(activity)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            val icon = when (activity) {
                                ActivityType.RUNNING -> Icons.Filled.DirectionsRun
                                ActivityType.WALKING -> Icons.Filled.DirectionsWalk
                                ActivityType.CYCLING -> Icons.Filled.DirectionsBike
                                ActivityType.GENERAL_WORKOUT -> Icons.Filled.FitnessCenter
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = activity.displayName,
                                tint = if (isSelected) OnAccent else TextGray,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = activity.displayName,
                                color = if (isSelected) OnAccent else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Main Primary Telemetry HUD Card
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackgroundElevated,
                borderColor = if (trackingState == TrackingState.TRACKING) BrandPrimary.copy(alpha = 0.45f) else CardBorder,
                contentPadding = PaddingValues(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DURATION",
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = metrics.formattedDuration,
                            color = TextWhite,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "DISTANCE",
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = metrics.formattedDistanceKm,
                                color = BrandAccentText,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "km",
                                color = TextGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CardBorderSubtle)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBlock(label = "SPEED", value = metrics.formattedSpeed, unit = "km/h")
                    MetricBlock(label = "AVG PACE", value = metrics.formattedPace, unit = "/km")
                    MetricBlock(label = "CALORIES", value = "${metrics.calories}", unit = "kcal")
                    MetricBlock(
                        label = "HEART RATE",
                        value = metrics.heartRate?.toString() ?: "--",
                        unit = "bpm",
                        valueColor = if (metrics.heartRate != null) BrandAccentText else TextGray
                    )
                }
            }

            // Live GPS Route Canvas Visualizer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(196.dp),
                shape = RoundedCornerShape(KinetraTheme.cardRadius),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LiveRouteCanvas(
                        route = routePoints,
                        gridColor = CanvasGridLine,
                        routeColor = BrandAccentText,
                        startColor = AthleticGreen,
                        modifier = Modifier.fillMaxSize()
                    )

                    // GPS status overlay pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackgroundElevated.copy(alpha = 0.92f))
                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GpsFixed,
                                contentDescription = "GPS",
                                tint = if (routePoints.isNotEmpty()) AthleticGreen else AthleticAmber,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (routePoints.isNotEmpty()) "GPS Active (${routePoints.size} pts)" else "Acquiring GPS Fix...",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Live Motion Sensor Stream (Genuine Hardware Telemetry)
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Sensors,
                            contentDescription = "IMU",
                            tint = BrandAccentText,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = "LIVE SENSOR STREAM",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Text(
                        text = if (isPhoneFallback) "Phone Accelerometer" else "Watch IMU",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val accel = liveMotion?.accel
                val gyro = liveMotion?.gyro

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "ACCEL (X, Y, Z)", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (accel != null) String.format("%.2f, %.2f, %.2f m/s²", accel.x, accel.y, accel.z) else "Waiting for sensor...",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "GYRO (X, Y, Z)", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (gyro != null) String.format("%.2f, %.2f, %.2f rad/s", gyro.x, gyro.y, gyro.z) else "Waiting for sensor...",
                            color = BrandAccentText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Control Buttons
            when (trackingState) {
                TrackingState.IDLE -> {
                    Button(
                        onClick = onStartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(KinetraTheme.buttonRadius)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start", tint = OnAccent)
                            Text(
                                text = "START ${selectedActivity.displayName.uppercase()}",
                                color = OnAccent,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                }

                TrackingState.TRACKING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onPauseClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(KinetraTheme.buttonRadius),
                            border = BorderStroke(1.dp, ButtonBorder),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBackground)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Filled.Pause, contentDescription = "Pause", tint = TextWhite)
                                Text("PAUSE", color = TextWhite, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = onFinishRequest,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(KinetraTheme.buttonRadius)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Filled.Stop, contentDescription = "Finish", tint = OnAccent)
                                Text("FINISH", color = OnAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                TrackingState.PAUSED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onResumeClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(KinetraTheme.buttonRadius)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Resume", tint = OnAccent)
                                Text("RESUME", color = OnAccent, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = onFinishRequest,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            border = BorderStroke(1.dp, BrandPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = AccentTintSurface),
                            shape = RoundedCornerShape(KinetraTheme.buttonRadius)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Save", tint = BrandAccentText)
                                Text("SAVE WORKOUT", color = BrandAccentText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                TrackingState.FINISHED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onResetClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            border = BorderStroke(1.dp, ButtonBorder),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBackground),
                            shape = RoundedCornerShape(KinetraTheme.buttonRadius)
                        ) {
                            Text("NEW WORKOUT", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = onDoneClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(KinetraTheme.buttonRadius)
                        ) {
                            Text("DONE", color = OnAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MetricBlock(
    label: String,
    value: String,
    unit: String,
    valueColor: Color = TextWhite
) {
    Column {
        Text(
            text = label,
            color = TextGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                color = valueColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = unit,
                color = TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
private fun LiveRouteCanvas(
    route: List<GpsPoint>,
    gridColor: Color,
    routeColor: Color,
    startColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val step = 36.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += step
        }

        if (route.size < 2) {
            drawCircle(
                color = routeColor.copy(alpha = 0.18f),
                radius = 44.dp.toPx(),
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawCircle(
                color = routeColor.copy(alpha = 0.08f),
                radius = 22.dp.toPx(),
                center = center
            )
            drawCircle(
                color = routeColor,
                radius = 5.5.dp.toPx(),
                center = center
            )
            return@Canvas
        }

        val minLat = route.minOf { it.latitude }
        val maxLat = route.maxOf { it.latitude }
        val minLon = route.minOf { it.longitude }
        val maxLon = route.maxOf { it.longitude }

        val latSpan = (maxLat - minLat).coerceAtLeast(0.0001)
        val lonSpan = (maxLon - minLon).coerceAtLeast(0.0001)

        val padding = 36.dp.toPx()
        val drawW = size.width - padding * 2
        val drawH = size.height - padding * 2

        val points = route.map { pt ->
            val normX = padding + ((pt.longitude - minLon) / lonSpan).toFloat() * drawW
            val normY = padding + (1f - ((pt.latitude - minLat) / latSpan).toFloat()) * drawH
            Offset(normX, normY)
        }

        val path = Path()
        path.moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
        }

        drawPath(
            path = path,
            color = routeColor,
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        drawCircle(
            color = startColor,
            radius = 5.5.dp.toPx(),
            center = points.first()
        )

        drawCircle(
            color = routeColor.copy(alpha = 0.28f),
            radius = 11.dp.toPx(),
            center = points.last()
        )
        drawCircle(
            color = routeColor,
            radius = 5.5.dp.toPx(),
            center = points.last()
        )
    }
}

private val previewMetrics = LiveMetrics(
    durationSeconds = 1714,
    distanceMeters = 6240.0,
    currentSpeedKmh = 12.8f,
    averagePaceSecondsPerKm = 275f,
    calories = 468,
    heartRate = 154
)

private val previewRoute = listOf(
    GpsPoint(51.5362, -0.0410),
    GpsPoint(51.5375, -0.0392),
    GpsPoint(51.5390, -0.0378),
    GpsPoint(51.5402, -0.0355),
    GpsPoint(51.5388, -0.0340)
)

@Preview(showBackground = true, name = "Live Tracking - Light")
@Composable
private fun LiveTrackingLightPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.LIGHT)) {
        LiveTrackingContent(
            selectedActivity = ActivityType.RUNNING,
            trackingState = TrackingState.TRACKING,
            metrics = previewMetrics,
            routePoints = previewRoute,
            isPhoneFallback = true,
            liveMotion = MotionReading(
                accel = AccelerometerReading(0.42f, 9.78f, -1.14f),
                gyro = GyroscopeReading(0.08f, -0.12f, 0.04f)
            ),
            onBack = {},
            onDevicesClick = {},
            onSelectActivity = {},
            onStartClick = {},
            onPauseClick = {},
            onResumeClick = {},
            onFinishRequest = {},
            onResetClick = {},
            onDoneClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Live Tracking - Dark")
@Composable
private fun LiveTrackingDarkPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.DARK)) {
        LiveTrackingContent(
            selectedActivity = ActivityType.RUNNING,
            trackingState = TrackingState.TRACKING,
            metrics = previewMetrics,
            routePoints = previewRoute,
            isPhoneFallback = false,
            liveMotion = MotionReading(
                accel = AccelerometerReading(0.42f, 9.78f, -1.14f),
                gyro = GyroscopeReading(0.08f, -0.12f, 0.04f)
            ),
            onBack = {},
            onDevicesClick = {},
            onSelectActivity = {},
            onStartClick = {},
            onPauseClick = {},
            onResumeClick = {},
            onFinishRequest = {},
            onResetClick = {},
            onDoneClick = {}
        )
    }
}
