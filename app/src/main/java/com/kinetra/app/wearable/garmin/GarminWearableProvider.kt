package com.kinetra.app.wearable.garmin

import android.content.Context
import com.kinetra.app.wearable.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Garmin smartwatch adapter prepared for Garmin Connect IQ Companion SDK.
 */
class GarminWearableProvider : WearableProvider {

    override val brand: WearableBrand = WearableBrand.GARMIN

    override val supportedCapabilities = DeviceCapabilities(
        hasHeartRate = true,
        hasAccelerometer = true,
        hasGyroscope = false,
        hasGps = true,
        hasBatteryLevel = true,
        hasStepCadence = true
    )

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _connectedDevice = MutableStateFlow<WearableDevice?>(null)
    override val connectedDevice: StateFlow<WearableDevice?> = _connectedDevice.asStateFlow()

    private val _heartRateStream = MutableSharedFlow<HeartRateReading>(replay = 1)
    override val heartRateStream: SharedFlow<HeartRateReading> = _heartRateStream.asSharedFlow()

    private val _accelerometerStream = MutableSharedFlow<AccelerometerReading>(replay = 1)
    override val accelerometerStream: SharedFlow<AccelerometerReading> = _accelerometerStream.asSharedFlow()

    private val _gyroscopeStream = MutableSharedFlow<GyroscopeReading>(replay = 1)
    override val gyroscopeStream: SharedFlow<GyroscopeReading> = _gyroscopeStream.asSharedFlow()

    override fun checkAvailability(context: Context): ProviderAvailability {
        return ProviderAvailability(
            isSupported = false,
            statusMessage = "Garmin smartwatches require Garmin Connect app and Connect IQ Companion SDK.",
            missingRequirements = listOf(
                "Garmin Connect Mobile app",
                "Connect IQ SDK manifest declaration"
            )
        )
    }

    override suspend fun initialize(context: Context): Boolean = false

    override suspend fun startScan(context: Context): List<WearableDevice> {
        return listOf(
            WearableDevice(
                id = "garmin_forerunner_stub",
                name = "Garmin Forerunner / Fenix",
                brand = WearableBrand.GARMIN,
                model = "Forerunner 965 / Fenix 7",
                connectionStatus = ConnectionStatus.DISCONNECTED,
                capabilities = supportedCapabilities,
                requiresAuthorization = true,
                authorizationStatusText = "Garmin Connect pairing required"
            )
        )
    }

    override suspend fun connect(context: Context, deviceId: String): Boolean = false

    override suspend fun disconnect() {
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
    }
}

