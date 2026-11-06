package com.kinetra.app.wearable.amazfit

import android.content.Context
import com.kinetra.app.wearable.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Amazfit / Zepp OS wearable provider adapter.
 */
class AmazfitWearableProvider : WearableProvider {

    override val brand: WearableBrand = WearableBrand.AMAZFIT

    override val supportedCapabilities = DeviceCapabilities(
        hasHeartRate = true,
        hasAccelerometer = true,
        hasGyroscope = true,
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
            statusMessage = "Amazfit smartwatches require Zepp app and Zepp OS Open Platform pairing.",
            missingRequirements = listOf(
                "Zepp app (formerly Amazfit)",
                "Zepp OS Bridge Service"
            )
        )
    }

    override suspend fun initialize(context: Context): Boolean = false

    override suspend fun startScan(context: Context): List<WearableDevice> {
        return listOf(
            WearableDevice(
                id = "amazfit_cheetah_stub",
                name = "Amazfit Cheetah / Balance",
                brand = WearableBrand.AMAZFIT,
                model = "Cheetah Pro / GTR 4",
                connectionStatus = ConnectionStatus.DISCONNECTED,
                capabilities = supportedCapabilities,
                requiresAuthorization = true,
                authorizationStatusText = "Zepp app pairing required"
            )
        )
    }

    override suspend fun connect(context: Context, deviceId: String): Boolean = false

    override suspend fun disconnect() {
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
    }
}

