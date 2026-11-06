package com.kinetra.app.tracking.model

import com.kinetra.app.wearable.WearableBrand

/**
 * Supported athletic tracking modes.
 */
enum class ActivityType(val displayName: String, val met: Float) {
    RUNNING("Running", 9.8f),
    WALKING("Walking", 3.8f),
    CYCLING("Cycling", 7.5f),
    GENERAL_WORKOUT("General Workout", 6.0f)
}

/**
 * State machine for live workouts.
 */
enum class TrackingState {
    IDLE,
    TRACKING,
    PAUSED,
    FINISHED
}

/**
 * High-precision GPS waypoint.
 */
data class GpsPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Live calculated metrics displayed on the HUD.
 */
data class LiveMetrics(
    val durationSeconds: Long = 0L,
    val distanceMeters: Double = 0.0,
    val currentSpeedKmh: Float = 0f,
    val averagePaceSecondsPerKm: Float = 0f,
    val calories: Int = 0,
    val heartRate: Int? = null,
    val heartRateZone: String = "Resting",
    val activeCadenceSpm: Int = 0,
    val sourceDeviceName: String = "Phone Internal Sensors"
) {
    val formattedDuration: String
        get() {
            val hours = durationSeconds / 3600
            val minutes = (durationSeconds % 3600) / 60
            val seconds = durationSeconds % 60
            return if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }

    val formattedDistanceKm: String
        get() = String.format("%.2f", distanceMeters / 1000.0)

    val formattedPace: String
        get() {
            if (distanceMeters < 20 || averagePaceSecondsPerKm <= 0 || averagePaceSecondsPerKm > 3600) {
                return "--'--\""
            }
            val paceMin = (averagePaceSecondsPerKm / 60).toInt()
            val paceSec = (averagePaceSecondsPerKm % 60).toInt()
            return String.format("%d'%02d\"", paceMin, paceSec)
        }

    val formattedSpeed: String
        get() = String.format("%.1f", currentSpeedKmh)
}

/**
 * Stored workout session record.
 */
data class WorkoutSession(
    val id: String,
    val activityType: ActivityType,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val distanceMeters: Double,
    val averageSpeedKmh: Float,
    val averagePaceFormatted: String,
    val caloriesBurned: Int,
    val averageHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val routeCoordinates: List<GpsPoint> = emptyList(),
    val wearableBrand: WearableBrand = WearableBrand.PHONE_FALLBACK,
    val isSyncedToBackend: Boolean = false
)

