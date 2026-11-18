package com.kinetra.app.ui.devices

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraIconButton
import com.kinetra.app.ui.components.KinetraScreenHeader
import com.kinetra.app.ui.components.SectionHeader
import com.kinetra.app.ui.theme.*
import com.kinetra.app.wearable.WearableBrand

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
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KinetraScreenHeader(
                title = "Connected Devices",
                subtitle = "KINETRA · WEARABLES",
                onBack = onBack,
                trailingContent = {
                    KinetraIconButton(
                        icon = Icons.Filled.Refresh,
                        onClick = { if (!isScanning) viewModel.scanDevices(context) },
                        contentDescription = "Scan Devices",
                        tint = if (isScanning) TextGray else BrandAccentText,
                        size = 40.dp
                    )
                }
            )

            // Current Active Device Card
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackgroundElevated,
                borderColor = if (!isPhoneFallback) BrandPrimary.copy(alpha = 0.5f) else CardBorder,
                contentPadding = PaddingValues(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTIVE SENSOR TELEMETRY",
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = activeDevice?.name ?: "Phone Internal Sensors",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(if (!isPhoneFallback) AthleticGreen.copy(alpha = 0.15f) else AccentTintSurface)
                            .border(
                                width = 1.dp,
                                color = if (!isPhoneFallback) AthleticGreen else BrandPrimary.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (!isPhoneFallback) "CONNECTED" else "PHONE FALLBACK",
                            color = if (!isPhoneFallback) AthleticGreen else BrandAccentText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CapabilityTag("HR Sensor", activeDevice?.capabilities?.hasHeartRate == true)
                    CapabilityTag("3-Axis Accel", activeDevice?.capabilities?.hasAccelerometer == true)
                    CapabilityTag("Gyroscope", activeDevice?.capabilities?.hasGyroscope == true)
                    CapabilityTag("GPS", activeDevice?.capabilities?.hasGps == true)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CardBorderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

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

            SectionHeader(title = "Huawei Wear Engine Integration")

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentTintSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Watch,
                                contentDescription = "Huawei Watch",
                                tint = BrandAccentText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Huawei Watch GT 4",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "HarmonyOS · TruSeen 5.5+ PPG",
                                color = TextGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val huaweiDevice = scannedDevices.firstOrNull { it.brand == WearableBrand.HUAWEI }
                            if (huaweiDevice != null) {
                                viewModel.connectDevice(context, huaweiDevice)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Connect", color = OnAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val avail = huaweiAvailability
                val isReady = avail?.isSupported == true
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isReady) AthleticGreen.copy(alpha = 0.1f) else AccentTintSurface)
                        .border(
                            width = 1.dp,
                            color = if (isReady) AthleticGreen.copy(alpha = 0.35f) else BrandPrimary.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isReady) Icons.Filled.CheckCircle else Icons.Filled.Info,
                            contentDescription = "Status",
                            tint = if (isReady) AthleticGreen else BrandAccentText,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = if (isReady) "Huawei Ecosystem Ready" else "Setup Prerequisites Required",
                                color = if (isReady) AthleticGreen else BrandAccentText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = avail?.statusMessage ?: "Checking Huawei Health and Wear Engine availability...",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showHuaweiRequirements = !showHuaweiRequirements },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showHuaweiRequirements) "Hide Technical Checklist" else "View Technical Checklist & Setup",
                        color = BrandAccentText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (showHuaweiRequirements) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = BrandAccentText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(visible = showHuaweiRequirements) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ChecklistItem("1. Huawei Health App (com.huawei.health)", avail?.missingRequirements?.none { it.contains("com.huawei.health") } == true)
                        ChecklistItem("2. Huawei Watch GT 4 Paired via Bluetooth in Huawei Health", false)
                        ChecklistItem("3. Wear Engine Sensor Permissions Granted by Athlete", false)
                        ChecklistItem("4. Huawei Developer Account & AppGallery Connect Verification", false)
                    }
                }
            }

            SectionHeader(title = "Additional Smartwatch Ecosystems")

            BrandCard("Samsung Galaxy Watch", "Samsung Health Services · Wear OS 4+", "Ready for Galaxy Health Services integration")
            BrandCard("Garmin Forerunner & Fenix", "Garmin Connect IQ Companion SDK", "Ready for Connect IQ telemetry integration")
            BrandCard("Amazfit Smartwatches", "Zepp OS Open Platform", "Ready for Zepp OS Bridge integration")

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = "Integrity",
                        tint = BrandAccentText,
                        modifier = Modifier.size(19.dp)
                    )
                    Text(
                        text = "Hardware Telemetry Integrity: KINETRA never fabricates synthetic heart rate readings. When a smartwatch is not connected, your smartphone's genuine accelerometer and GPS serve as a transparent fallback.",
                        color = TextGray,
                        fontSize = 11.sp,
                        lineHeight = 17.sp
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
            .background(if (isSupported) AccentTintSurface else SurfaceDark)
            .border(
                width = 1.dp,
                color = if (isSupported) BrandPrimary.copy(alpha = 0.3f) else CardBorderSubtle,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 7.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (isSupported) BrandAccentText else TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
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
    KinetraCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = ecosystem, color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = notes, color = TextGray, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceDark)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "PLANNED", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
