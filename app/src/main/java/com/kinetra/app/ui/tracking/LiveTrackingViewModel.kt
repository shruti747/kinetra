package com.kinetra.app.ui.tracking

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kinetra.app.tracking.model.*
import com.kinetra.app.tracking.repository.WorkoutRepository
import com.kinetra.app.tracking.service.TrackingForegroundService
import com.kinetra.app.wearable.WearableBrand
import com.kinetra.app.wearable.WearableManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * ViewModel for real-time sports activity tracking.
 */
class LiveTrackingViewModel : ViewModel() {

    private val _selectedActivity = MutableStateFlow(ActivityType.RUNNING)
    val selectedActivity: StateFlow<ActivityType> = _selectedActivity.asStateFlow()

    val trackingState = TrackingForegroundService.trackingState
    val liveMetrics = TrackingForegroundService.liveMetrics
    val routePoints = TrackingForegroundService.routePoints

    val activeDevice = WearableManager.activeDevice
    val isUsingPhoneFallback = WearableManager.isUsingPhoneFallback
    val liveMotion = WearableManager.currentMotion

    private var workoutStartTime: Long = 0L

    fun setActivity(type: ActivityType) {
        if (trackingState.value == TrackingState.IDLE) {
            _selectedActivity.value = type
        }
    }

    fun startTracking(context: Context) {
        if (trackingState.value == TrackingState.TRACKING) return
        workoutStartTime = System.currentTimeMillis()
        TrackingForegroundService.start(context, _selectedActivity.value)
    }

    fun pauseTracking(context: Context) {
        if (trackingState.value != TrackingState.TRACKING) return
        TrackingForegroundService.pause(context)
    }

    fun resumeTracking(context: Context) {
        if (trackingState.value != TrackingState.PAUSED) return
        TrackingForegroundService.resume(context)
    }

    fun stopTracking(context: Context): WorkoutSession? {
        if (trackingState.value == TrackingState.IDLE) return null
        val currentMetrics = liveMetrics.value
        val currentRoute = routePoints.value
        val endTime = System.currentTimeMillis()
        val duration = currentMetrics.durationSeconds

        TrackingForegroundService.stop(context)

        if (duration < 3) return null

        val session = WorkoutSession(
            id = "wk_${UUID.randomUUID().toString().take(8)}",
            activityType = _selectedActivity.value,
            startTimeMillis = workoutStartTime,
            endTimeMillis = endTime,
            durationSeconds = duration,
            distanceMeters = currentMetrics.distanceMeters,
            averageSpeedKmh = currentMetrics.currentSpeedKmh,
            averagePaceFormatted = currentMetrics.formattedPace,
            caloriesBurned = currentMetrics.calories,
            averageHeartRate = currentMetrics.heartRate,
            maxHeartRate = currentMetrics.heartRate?.let { it + 12 },
            routeCoordinates = currentRoute,
            wearableBrand = if (isUsingPhoneFallback.value) WearableBrand.PHONE_FALLBACK else WearableBrand.HUAWEI,
            isSyncedToBackend = false
        )

        WorkoutRepository.saveWorkout(session)
        return session
    }

    fun resetTracking() {
        TrackingForegroundService.resetToIdle()
    }
}

