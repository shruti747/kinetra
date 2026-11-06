package com.kinetra.app.tracking.engine

import android.location.Location
import com.kinetra.app.tracking.model.ActivityType
import com.kinetra.app.tracking.model.GpsPoint
import com.kinetra.app.tracking.model.LiveMetrics

/**
 * High-performance sports telemetry computation engine.
 */
object TrackingEngine {

    private const val DEFAULT_ATHLETE_WEIGHT_KG = 68f

    fun calculateDistance(p1: GpsPoint, p2: GpsPoint): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            p1.latitude, p1.longitude,
            p2.latitude, p2.longitude,
            results
        )
        return results[0].toDouble()
    }

    fun calculatePaceSecondsPerKm(distanceMeters: Double, durationSeconds: Long): Float {
        if (distanceMeters < 30 || durationSeconds <= 0) return 0f
        val distanceKm = distanceMeters / 1000.0
        return (durationSeconds / distanceKm).toFloat()
    }

    fun calculateCalories(activity: ActivityType, durationSeconds: Long, weightKg: Float = DEFAULT_ATHLETE_WEIGHT_KG): Int {
        val durationMinutes = durationSeconds / 60.0
        val caloriesPerMinute = (activity.met * 3.5 * weightKg) / 200.0
        return (caloriesPerMinute * durationMinutes).toInt()
    }

    fun determineHeartRateZone(bpm: Int?): String {
        bpm ?: return "N/A"
        return when {
            bpm < 115 -> "Warm-up"
            bpm < 135 -> "Fat Burn"
            bpm < 155 -> "Aerobic Cardio"
            bpm < 175 -> "Anaerobic"
            else -> "Peak Effort"
        }
    }
}

