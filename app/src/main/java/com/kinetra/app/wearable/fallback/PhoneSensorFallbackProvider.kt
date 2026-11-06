package com.kinetra.app.wearable.fallback

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.kinetra.app.wearable.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Genuine hardware fallback provider utilizing the smartphone's built-in sensors.
 *
 * Provides real-time Accelerometer and Gyroscope readings when no external smartwatch
 * is paired or when watch sensors are temporarily unavailable.
 */
class PhoneSensorFallbackProvider : WearableProvider, SensorEventListener {

    override val brand: WearableBrand = WearableBrand.PHONE_FALLBACK

    override val supportedCapabilities = DeviceCapabilities(
        hasHeartRate = false, // Standard phones do not have wearable optical PPG
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

    private var sensorManager: SensorManager? = null
    private var accelSensor: Sensor? = null
    private var gyroSensor: Sensor? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override suspend fun initialize(context: Context): Boolean {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val device = WearableDevice(
            id = "phone_internal_sensors",
            name = "Phone Internal Motion Sensors",
            brand = WearableBrand.PHONE_FALLBACK,
            model = android.os.Build.MODEL,
            connectionStatus = ConnectionStatus.CONNECTED,
            capabilities = supportedCapabilities.copy(
                hasGyroscope = gyroSensor != null
            ),
            batteryLevel = 100,
            lastSyncTimeMillis = System.currentTimeMillis(),
            authorizationStatusText = "Active Fallback"
        )
        _connectedDevice.value = device
        _connectionStatus.value = ConnectionStatus.CONNECTED

        startSensorListening()
        return true
    }

    private fun startSensorListening() {
        val sm = sensorManager ?: return
        accelSensor?.let {
            sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroSensor?.let {
            sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val reading = AccelerometerReading(
                    x = event.values.getOrElse(0) { 0f },
                    y = event.values.getOrElse(1) { 0f },
                    z = event.values.getOrElse(2) { 0f },
                    timestamp = System.currentTimeMillis()
                )
                scope.launch { _accelerometerStream.emit(reading) }
            }
            Sensor.TYPE_GYROSCOPE -> {
                val reading = GyroscopeReading(
                    x = event.values.getOrElse(0) { 0f },
                    y = event.values.getOrElse(1) { 0f },
                    z = event.values.getOrElse(2) { 0f },
                    timestamp = System.currentTimeMillis()
                )
                scope.launch { _gyroscopeStream.emit(reading) }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override suspend fun startScan(context: Context): List<WearableDevice> {
        return listOfNotNull(_connectedDevice.value)
    }

    override suspend fun connect(context: Context, deviceId: String): Boolean {
        startSensorListening()
        _connectionStatus.value = ConnectionStatus.CONNECTED
        return true
    }

    override suspend fun disconnect() {
        sensorManager?.unregisterListener(this)
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
    }

    override fun checkAvailability(context: Context): ProviderAvailability {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val hasAccel = sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        return ProviderAvailability(
            isSupported = hasAccel,
            statusMessage = if (hasAccel) "Phone internal sensors available for fallback tracking."
            else "Phone lacks standard accelerometer hardware.",
            missingRequirements = emptyList()
        )
    }
}

