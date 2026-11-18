package com.kinetra.app.tracking.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.kinetra.app.MainActivity
import com.kinetra.app.R
import com.kinetra.app.tracking.engine.TrackingEngine
import com.kinetra.app.tracking.model.ActivityType
import com.kinetra.app.tracking.model.GpsPoint
import com.kinetra.app.tracking.model.LiveMetrics
import com.kinetra.app.tracking.model.TrackingState
import com.kinetra.app.wearable.WearableManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Android 14-compliant Foreground Service for uninterrupted workout tracking.
 *
 * Configured with FOREGROUND_SERVICE_TYPE_LOCATION to track real-time athlete
 * coordinates, speed, pace, and sensor metrics safely without permission crashes.
 */
class TrackingForegroundService : Service(), LocationListener {

    companion object {
        const val CHANNEL_ID = "kinetra_live_tracking_channel"
        const val NOTIFICATION_ID = 2024

        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_ACTIVITY_TYPE = "EXTRA_ACTIVITY_TYPE"

        private val _trackingState = MutableStateFlow(TrackingState.IDLE)
        val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()

        private val _liveMetrics = MutableStateFlow(LiveMetrics())
        val liveMetrics: StateFlow<LiveMetrics> = _liveMetrics.asStateFlow()

        private val _routePoints = MutableStateFlow<List<GpsPoint>>(emptyList())
        val routePoints: StateFlow<List<GpsPoint>> = _routePoints.asStateFlow()

        fun start(context: Context, activityType: ActivityType) {
            val intent = Intent(context, TrackingForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVITY_TYPE, activityType.name)
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {
                // Prevent crash if background execution is restricted by OEM
            }
        }

        fun pause(context: Context) {
            try {
                context.startService(Intent(context, TrackingForegroundService::class.java).apply { action = ACTION_PAUSE })
            } catch (_: Exception) {}
        }

        fun resume(context: Context) {
            try {
                context.startService(Intent(context, TrackingForegroundService::class.java).apply { action = ACTION_RESUME })
            } catch (_: Exception) {}
        }

        fun stop(context: Context) {
            try {
                context.startService(Intent(context, TrackingForegroundService::class.java).apply { action = ACTION_STOP })
            } catch (_: Exception) {}
        }

        fun resetToIdle() {
            _trackingState.value = TrackingState.IDLE
            _liveMetrics.value = LiveMetrics()
            _routePoints.value = emptyList()
        }
    }

    private var locationManager: LocationManager? = null
    private var activityType = ActivityType.RUNNING
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private var durationSeconds = 0L
    private var distanceMeters = 0.0
    private var lastLocation: Location? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val typeName = intent.getStringExtra(EXTRA_ACTIVITY_TYPE) ?: ActivityType.RUNNING.name
                activityType = try { ActivityType.valueOf(typeName) } catch (_: Exception) { ActivityType.RUNNING }
                startTracking()
            }
            ACTION_PAUSE -> pauseTracking()
            ACTION_RESUME -> resumeTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    private fun startTracking() {
        durationSeconds = 0L
        distanceMeters = 0.0
        lastLocation = null
        _routePoints.value = emptyList()
        _trackingState.value = TrackingState.TRACKING

        startForegroundWithNotification()
        startLocationUpdates()
        startTimer()
    }

    private fun pauseTracking() {
        _trackingState.value = TrackingState.PAUSED
        timerJob?.cancel()
        timerJob = null
        updateNotification()
    }

    private fun resumeTracking() {
        _trackingState.value = TrackingState.TRACKING
        startTimer()
        updateNotification()
    }

    private fun stopTracking() {
        _trackingState.value = TrackingState.FINISHED
        timerJob?.cancel()
        timerJob = null
        stopLocationUpdates()
        try {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (_: Exception) {}
        stopSelf()
    }

    private fun startForegroundWithNotification() {
        createNotificationChannel()
        val notification = buildNotification()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // In Android 14 (API 34), FOREGROUND_SERVICE_TYPE_LOCATION requires FOREGROUND_SERVICE_LOCATION
                // and ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION.
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            // Safe fallback to basic startForeground
            try {
                startForeground(NOTIFICATION_ID, notification)
            } catch (_: Exception) {}
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive && _trackingState.value == TrackingState.TRACKING) {
                delay(1000)
                durationSeconds++
                recalculateMetrics()
                if (durationSeconds % 3 == 0L) {
                    updateNotification()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val lm = locationManager ?: return
        try {
            val hasFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (!hasFine && !hasCoarse) return

            if (hasFine && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1.5f, this)
            }
            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 3f, this)
            }
        } catch (_: SecurityException) {
            // Permission missing or revoked
        } catch (_: Exception) {
            // Provider disabled or restricted
        }
    }

    private fun stopLocationUpdates() {
        try {
            locationManager?.removeUpdates(this)
        } catch (_: Exception) {}
    }

    override fun onLocationChanged(location: Location) {
        if (_trackingState.value != TrackingState.TRACKING) return
        if (location.accuracy > 40f) return // filter low-accuracy fixes

        val last = lastLocation
        if (last != null) {
            val segment = last.distanceTo(location).toDouble()
            if (segment > 1.0) {
                distanceMeters += segment
            }
        }
        lastLocation = location

        val point = GpsPoint(location.latitude, location.longitude, location.altitude)
        _routePoints.value = _routePoints.value + point

        recalculateMetrics()
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    private fun recalculateMetrics() {
        val speedKmh = lastLocation?.speed?.let { it * 3.6f } ?: 0f
        val pace = TrackingEngine.calculatePaceSecondsPerKm(distanceMeters, durationSeconds)
        val calories = TrackingEngine.calculateCalories(activityType, durationSeconds)
        val hr = WearableManager.currentHeartRate.value
        val hrZone = TrackingEngine.determineHeartRateZone(hr)
        val source = WearableManager.activeDevice.value?.name ?: "Phone Sensors"

        _liveMetrics.value = LiveMetrics(
            durationSeconds = durationSeconds,
            distanceMeters = distanceMeters,
            currentSpeedKmh = speedKmh,
            averagePaceSecondsPerKm = pace,
            calories = calories,
            heartRate = hr,
            heartRateZone = hrZone,
            sourceDeviceName = source
        )
    }

    private fun buildNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val metrics = _liveMetrics.value
        val hrText = if (metrics.heartRate != null) " • ${metrics.heartRate} bpm" else ""
        val contentText = "${metrics.formattedDuration} | ${metrics.formattedDistanceKm} km | Pace: ${metrics.formattedPace}$hrText"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("KINETRA — ${activityType.displayName}")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_kinetra_notification)
            .setColor(0xFF800020.toInt())
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification() {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.notify(NOTIFICATION_ID, buildNotification())
        } catch (_: Exception) {}
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Live Sports Tracking",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows real-time live metrics and duration during sports workouts"
                    setShowBadge(false)
                }
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.createNotificationChannel(channel)
            } catch (_: Exception) {}
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        timerJob?.cancel()
        timerJob = null
        stopLocationUpdates()
        super.onDestroy()
    }
}
