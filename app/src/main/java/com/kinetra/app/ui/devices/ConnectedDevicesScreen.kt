package com.kinetra.app.ui.devices

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.theme.*
import com.kinetra.app.wearable.ConnectionStatus
import com.kinetra.app.wearable.WearableBrand
import com.kinetra.app.wearable.WearableDevice

@Composable
fun ConnectedDevicesScreen(
    viewModel: ConnectedDevicesViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val activeDevice by viewModel.activeDevice.collectAsState()
    val isPhoneFallback by viewModel.isUsingPhoneFallback.collectAsState()
    val scannedDevices by viewModel.scannedDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val huaweiAvailability by viewModel.huaweiAvailability.collectAsState()

    var showHuaweiRequirements by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkPrerequisites(context)
        viewModel.scanDevices(context)
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
            // Header
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
                            .clickable { onBack() },
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
                        text = "CONNECTED DEVICES",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.scanDevices(context) },
                    enabled = !isScanning
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh",
                        tint = if (isScanning) TextGray else ElectricCyan
                    )
                }
            }

            // Current Active Device Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundElevated),
                border = BorderStroke(1.dp, if (!isPhoneFallback) ElectricCyan else CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE SENSOR TELEMETRY",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activeDevice?.name ?: "Phone Internal Sensors",
                                color = TextWhite,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(if (!isPhoneFallback) AthleticGreen.copy(alpha = 0.2f) else AthleticAmber.copy(alpha = 0.2f))
                                .border(1.dp, if (!isPhoneFallback) AthleticGreen else AthleticAmber, RoundedCornerShape(50.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (!isPhoneFallback) "WATCH CONNECTED" else "FALLBACK ACTIVE",
                                color = if (!isPhoneFallback) AthleticGreen else AthleticAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Capability Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CapabilityTag("HR Sensor", activeDevice?.capabilities?.hasHeartRate == true)
                        CapabilityTag("3-Axis Accel", activeDevice?.capabilities?.hasAccelerometer == true)
                        CapabilityTag("Gyroscope", activeDevice?.capabilities?.hasGyroscope == true)
                        CapabilityTag("GPS", activeDevice?.capabilities?.hasGps == true)
                    }

                    HorizontalDivider(color = CardBorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Model: ${activeDevice?.model ?: "Android Device"}",
                            color = TextGray,
                            fontSize = 12.sp
                        )

                        if (!isPhoneFallback) {
                            TextButton(
                                onClick = { viewModel.disconnectDevice() },
                                colors = ButtonDefaults.textButtonColors(contentColor = AthleticRed)
                            ) {
                                Text("Disconnect", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Target Primary Wearable: Huawei Watch GT 4 Section
            Text(
                text = "HUAWEI WEAR ENGINE INTEGRATION",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Watch,
                                contentDescription = "Huawei Watch",
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Huawei Watch GT 4",
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "HarmonyOS / TruSeen 5.5+ Optical PPG",
                                    color = TextGray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val huaweiDevice = scannedDevices.firstOrNull { it.brand == WearableBrand.HUAWEI }
                                if (huaweiDevice != null) {
                                    viewModel.connectDevice(context, huaweiDevice)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text("Connect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Prerequisites status text
                    val avail = huaweiAvailability
                    val isReady = avail?.isSupported == true
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isReady) AthleticGreen.copy(alpha = 0.1f) else AthleticAmber.copy(alpha = 0.1f))
                            .border(1.dp, if (isReady) AthleticGreen.copy(alpha = 0.4f) else AthleticAmber.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isReady) Icons.Filled.CheckCircle else Icons.Filled.Info,
                                contentDescription = "Status",
                                tint = if (isReady) AthleticGreen else AthleticAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = if (isReady) "Huawei Ecosystem Ready" else "Setup Prerequisites Required",
                                    color = if (isReady) AthleticGreen else AthleticAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = avail?.statusMessage ?: "Checking Huawei Health and Wear Engine availability...",
                                    color = TextGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Toggle developer/hardware checklist
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showHuaweiRequirements = !showHuaweiRequirements },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showHuaweiRequirements) "Hide Technical Checklist" else "View Technical Checklist & Setup",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = if (showHuaweiRequirements) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Toggle",
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    AnimatedVisibility(visible = showHuaweiRequirements) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ChecklistItem("1. Huawei Health App (com.huawei.health)", avail?.missingRequirements?.none { it.contains("com.huawei.health") } == true)
                            ChecklistItem("2. Huawei Watch GT 4 Paired via Bluetooth in Huawei Health", false)
                            ChecklistItem("3. Wear Engine Sensor Permissions Granted by Athlete", false)
                            ChecklistItem("4. Huawei Developer Account & AppGallery Connect Verification", false)
                        }
                    }
                }
            }

            // Multi-Brand Expansion Shelf
            Text(
                text = "ADDITIONAL SMARTWATCH ECOSYSTEMS",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            BrandCard("Samsung Galaxy Watch", "Samsung Health Services / Wear OS 4+", "Ready for Galaxy Health Services integration")
            BrandCard("Garmin Forerunner & Fenix", "Garmin Connect IQ Companion SDK", "Ready for Connect IQ telemetry integration")
            BrandCard("Amazfit Smartwatches", "Zepp OS Open Platform", "Ready for Zepp OS Bridge integration")

            // Real Data Guarantee Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = "Integrity",
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Hardware Telemetry Integrity: KINETRA never generates synthetic or simulated heart rate readings. When a physical smartwatch is not connected, the system uses your phone's genuine internal accelerometer and GPS as a transparent fallback.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CapabilityTag(label: String, isSupported: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSupported) CardBackground else ButtonBackground)
            .border(1.dp, if (isSupported) CardBorderBright.copy(alpha = 0.5f) else CardBorderSubtle, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = if (isSupported) ElectricCyanGlow else TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ChecklistItem(label: String, isComplete: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isComplete) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isComplete) AthleticGreen else TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            color = if (isComplete) TextWhite else TextGray,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun BrandCard(title: String, ecosystem: String, notes: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ButtonBackground),
        border = BorderStroke(1.dp, ButtonBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = ecosystem, color = TextGray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = notes, color = TextMuted, fontSize = 10.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CardBackground)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "PLANNED", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

