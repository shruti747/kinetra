package com.kinetra.app.wearable

import android.content.Context
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Standard hardware abstraction interface for wearable smartwatches and sensors.
 *
 * All manufacturer-specific SDKs (Huawei Wear Engine, Samsung Health Services,
 * Garmin Connect IQ, Zepp OS) implement this interface. The tracking engine
 * consumes only this interface, keeping the core analytics independent of any brand.
 */
interface WearableProvider {
    /** The brand/manufacturer represented by this provider */
    val brand: WearableBrand

    /** Hardware capabilities offered by this provider */
    val supportedCapabilities: DeviceCapabilities

    /** Connection state observable */
    val connectionStatus: StateFlow<ConnectionStatus>

    /** Currently connected device information (null if disconnected) */
    val connectedDevice: StateFlow<WearableDevice?>

    /** Live streaming heart-rate updates */
    val heartRateStream: SharedFlow<HeartRateReading>

    /** Live streaming 3-axis accelerometer readings */
    val accelerometerStream: SharedFlow<AccelerometerReading>

    /** Live streaming 3-axis gyroscope readings */
    val gyroscopeStream: SharedFlow<GyroscopeReading>

    /**
     * Initializes any local or SDK resources needed by the provider.
     */
    suspend fun initialize(context: Context): Boolean

    /**
     * Scans for paired or nearby devices belonging to this ecosystem.
     */
    suspend fun startScan(context: Context): List<WearableDevice>

    /**
     * Attempts to connect to a specific wearable device.
     */
    suspend fun connect(context: Context, deviceId: String): Boolean

    /**
     * Disconnects from the current device and releases sensor subscriptions.
     */
    suspend fun disconnect()

    /**
     * Evaluates whether the host phone meets the technical and ecosystem prerequisites.
     */
    fun checkAvailability(context: Context): ProviderAvailability
}

