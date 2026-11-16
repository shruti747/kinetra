package com.kinetra.app.ui.tracking

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.kinetra.app.tracking.model.ActivityType
import com.kinetra.app.tracking.model.GpsPoint
import com.kinetra.app.tracking.model.TrackingState
import com.kinetra.app.ui.theme.*

@Composable
fun LiveTrackingScreen(
    viewModel: LiveTrackingViewModel,
    onBack: () -> Unit,
    onDevicesClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedActivity by viewModel.selectedActivity.collectAsState()
    val trackingState by viewModel.trackingState.collectAsState()
    val metrics by viewModel.liveMetrics.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val activeDevice by viewModel.activeDevice.collectAsState()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ButtonBackground)
                            .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                if (trackingState == TrackingState.FINISHED) {
                                    viewModel.resetTracking()
                                }
                                onBack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "LIVE ACTIVITY TRACKING",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Wearable Connection Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isPhoneFallback) ButtonBackground else ElectricBlue.copy(alpha = 0.2f))
                        .border(1.dp, if (isPhoneFallback) ButtonBorder else ElectricCyan, RoundedCornerShape(50.dp))
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
                            text = if (isPhoneFallback) "Phone Sensors" else "GT 4 Connected",
                            color = if (isPhoneFallback) TextGray else ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Activity Mode Selector (Enabled only before start)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActivityType.values().forEach { activity ->
                    val isSelected = selectedActivity == activity
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricBlue else ButtonBackground)
                            .border(1.dp, if (isSelected) ElectricCyan else ButtonBorder, RoundedCornerShape(12.dp))
                            .clickable(enabled = trackingState == TrackingState.IDLE) {
                                viewModel.setActivity(activity)
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                tint = if (isSelected) TextWhite else TextGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = activity.displayName,
                                color = if (isSelected) TextWhite else TextGray,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Main Primary HUD Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundElevated),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Duration & Distance Hero
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WORKOUT DURATION",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = metrics.formattedDuration,
                                color = TextWhite,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "DISTANCE",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = metrics.formattedDistanceKm,
                                    color = ElectricCyan,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "km",
                                    color = TextGray,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = CardBorderSubtle)

                    // Secondary Metrics Row: Speed, Pace, Calories, Heart Rate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBlock(
                            label = "SPEED",
                            value = metrics.formattedSpeed,
                            unit = "km/h"
                        )
                        MetricBlock(
                            label = "AVG PACE",
                            value = metrics.formattedPace,
                            unit = "/km"
                        )
                        MetricBlock(
                            label = "CALORIES",
                            value = "${metrics.calories}",
                            unit = "kcal"
                        )
                        MetricBlock(
                            label = "HEART RATE",
                            value = metrics.heartRate?.toString() ?: "--",
                            unit = "bpm",
                            valueColor = if (metrics.heartRate != null) AthleticRed else TextGray
                        )
                    }
                }
            }

            // Live GPS Route Canvas Visualizer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LiveRouteCanvas(
                        route = routePoints,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Status overlay in map
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark.copy(alpha = 0.85f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GpsFixed,
                                contentDescription = "GPS",
                                tint = if (routePoints.isNotEmpty()) AthleticGreen else AthleticAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (routePoints.isNotEmpty()) "GPS Active (${routePoints.size} pts)" else "Acquiring GPS Fix...",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Live Motion Sensor Stream (Genuine Hardware Sensor Data)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "LIVE HARDWARE SENSOR STREAM",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = if (isPhoneFallback) "Phone Accelerometer" else "Huawei GT 4 IMU",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    val accel = liveMotion?.accel
                    val gyro = liveMotion?.gyro

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "ACCEL (X, Y, Z)", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (accel != null) String.format("%.2f, %.2f, %.2f m/s²", accel.x, accel.y, accel.z) else "Waiting for sensor...",
                                color = ElectricCyanGlow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "GYRO (X, Y, Z)", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (gyro != null) String.format("%.2f, %.2f, %.2f rad/s", gyro.x, gyro.y, gyro.z) else "Waiting for sensor...",
                                color = AthleticGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Control Buttons
            when (trackingState) {
                TrackingState.IDLE -> {
                    Button(
                        onClick = {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AthleticGreen),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start", tint = BackgroundDark)
                            Text(
                                text = "START ${selectedActivity.displayName.uppercase()}",
                                color = BackgroundDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                TrackingState.TRACKING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.pauseTracking(context) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AthleticAmber),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Filled.Pause, contentDescription = "Pause", tint = BackgroundDark)
                                Text("PAUSE", color = BackgroundDark, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { showStopDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AthleticRed),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Filled.Stop, contentDescription = "Stop", tint = TextWhite)
                                Text("FINISH", color = TextWhite, fontWeight = FontWeight.Bold)
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
                            onClick = { viewModel.resumeTracking(context) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AthleticGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Resume", tint = BackgroundDark)
                                Text("RESUME", color = BackgroundDark, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { showStopDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AthleticRed),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Save", tint = TextWhite)
                                Text("SAVE RUN", color = TextWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                TrackingState.FINISHED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.resetTracking() },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AthleticGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("NEW WORKOUT", color = BackgroundDark, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.resetTracking()
                                onBack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("DONE", color = TextWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Stop Workout Confirmation Dialog
    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text("Finish Workout?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you ready to complete and save this workout session to your history?",
                    color = TextGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showStopDialog = false
                        viewModel.stopTracking(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AthleticGreen)
                ) {
                    Text("Complete & Save", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) {
                    Text("Continue Running", color = TextGray)
                }
            },
            containerColor = CardBackgroundElevated
        )
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
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                color = valueColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
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
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        // Draw background grid lines
        val step = 40.dp.toPx()
        val gridColor = Color(0xFF131D33)
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
            // Draw center radar circle when waiting for GPS
            drawCircle(
                color = ElectricBlue.copy(alpha = 0.2f),
                radius = 50.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = ElectricCyan,
                radius = 6.dp.toPx(),
                center = center
            )
            return@Canvas
        }

        // Normalize GPS points to canvas dimensions with 30dp padding
        val minLat = route.minOf { it.latitude }
        val maxLat = route.maxOf { it.latitude }
        val minLon = route.minOf { it.longitude }
        val maxLon = route.maxOf { it.longitude }

        val latSpan = (maxLat - minLat).coerceAtLeast(0.0001)
        val lonSpan = (maxLon - minLon).coerceAtLeast(0.0001)

        val padding = 40.dp.toPx()
        val drawW = size.width - padding * 2
        val drawH = size.height - padding * 2

        val points = route.map { pt ->
            val normX = padding + ((pt.longitude - minLon) / lonSpan).toFloat() * drawW
            val normY = padding + (1f - ((pt.latitude - minLat) / latSpan).toFloat()) * drawH
            Offset(normX, normY)
        }

        // Draw Polyline
        val path = Path()
        path.moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
        }

        drawPath(
            path = path,
            color = ElectricCyan,
            style = Stroke(width = 4.dp.toPx())
        )

        // Draw Start Pin
        drawCircle(
            color = AthleticGreen,
            radius = 6.dp.toPx(),
            center = points.first()
        )

        // Draw Current Position Head
        drawCircle(
            color = ElectricCyanGlow.copy(alpha = 0.4f),
            radius = 12.dp.toPx(),
            center = points.last()
        )
        drawCircle(
            color = ElectricCyan,
            radius = 6.dp.toPx(),
            center = points.last()
        )
    }
}

