package com.kinetra.app.wearable

/**
 * Supported wearable smartwatch brands in the Kinetra multi-brand architecture.
 */
enum class WearableBrand(val displayName: String, val ecosystem: String) {
    HUAWEI("Huawei Watch GT 4", "Huawei Wear Engine / HarmonyOS"),
    SAMSUNG("Samsung Galaxy Watch", "Samsung Health Services / Wear OS"),
    GARMIN("Garmin Smartwatch", "Garmin Connect IQ Companion SDK"),
    AMAZFIT("Amazfit Smartwatch", "Zepp OS Open Platform"),
    PHONE_FALLBACK("Phone Internal Sensors", "Android Hardware SensorManager")
}

/**
 * Live hardware connection states.
 */
enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

/**
 * Explicit capabilities supported by a specific wearable or provider.
 */
data class DeviceCapabilities(
    val hasHeartRate: Boolean = false,
    val hasAccelerometer: Boolean = false,
    val hasGyroscope: Boolean = false,
    val hasGps: Boolean = false,
    val hasBatteryLevel: Boolean = false,
    val hasStepCadence: Boolean = false,
    val hasSpO2: Boolean = false
)

/**
 * Common representation of a smartwatch or wearable device.
 */
data class WearableDevice(
    val id: String,
    val name: String,
    val brand: WearableBrand,
    val model: String,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val capabilities: DeviceCapabilities,
    val batteryLevel: Int? = null,
    val lastSyncTimeMillis: Long? = null,
    val requiresAuthorization: Boolean = false,
    val authorizationStatusText: String = "",
    val errorMessage: String? = null
)

/**
 * Raw 3-axis Accelerometer sensor reading.
 */
data class AccelerometerReading(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Raw 3-axis Gyroscope sensor reading.
 */
data class GyroscopeReading(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Heart-rate reading in beats per minute (BPM).
 */
data class HeartRateReading(
    val bpm: Int,
    val accuracy: Int = 3,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * High-precision GPS location reading.
 */
data class GpsLocationReading(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val accuracyMeters: Float = 0f,
    val speedKmh: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Unified motion sensor packet combining accelerometer and gyroscope.
 */
data class MotionReading(
    val accel: AccelerometerReading? = null,
    val gyro: GyroscopeReading? = null,
    val isPhoneFallback: Boolean = false
)

/**
 * Host device compatibility and setup check result.
 */
data class ProviderAvailability(
    val isSupported: Boolean,
    val statusMessage: String,
    val missingRequirements: List<String> = emptyList(),
    val setupActionNeeded: String? = null
)

