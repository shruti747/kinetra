package com.kinetra.app.wearable

import android.content.Context
import com.kinetra.app.wearable.amazfit.AmazfitWearableProvider
import com.kinetra.app.wearable.fallback.PhoneSensorFallbackProvider
import com.kinetra.app.wearable.garmin.GarminWearableProvider
import com.kinetra.app.wearable.huawei.HuaweiWearableProvider
import com.kinetra.app.wearable.samsung.SamsungGalaxyWatchProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Central wearable manager and sensor multiplexer.
 *
 * Coordinates multi-brand smartwatches, handles fallback to phone sensors,
 * and feeds normalized telemetry to the tracking engine.
 */
object WearableManager {

    private val scope = CoroutineScope(Dispatchers.Default)

    val huaweiProvider = HuaweiWearableProvider()
    val samsungProvider = SamsungGalaxyWatchProvider()
    val garminProvider = GarminWearableProvider()
    val amazfitProvider = AmazfitWearableProvider()
    val phoneFallbackProvider = PhoneSensorFallbackProvider()

    private val providers = listOf(
        huaweiProvider,
        samsungProvider,
        garminProvider,
        amazfitProvider,
        phoneFallbackProvider
    )

    private val _activeDevice = MutableStateFlow<WearableDevice?>(null)
    val activeDevice: StateFlow<WearableDevice?> = _activeDevice.asStateFlow()

    private val _isUsingPhoneFallback = MutableStateFlow(true)
    val isUsingPhoneFallback: StateFlow<Boolean> = _isUsingPhoneFallback.asStateFlow()

    private val _currentHeartRate = MutableStateFlow<Int?>(null)
    val currentHeartRate: StateFlow<Int?> = _currentHeartRate.asStateFlow()

    private val _currentMotion = MutableStateFlow<MotionReading?>(null)
    val currentMotion: StateFlow<MotionReading?> = _currentMotion.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<WearableDevice>>(emptyList())
    val scannedDevices: StateFlow<List<WearableDevice>> = _scannedDevices.asStateFlow()

    private var currentAccel: AccelerometerReading? = null
    private var currentGyro: GyroscopeReading? = null

    fun initialize(context: Context) {
        scope.launch {
            // Initialize phone fallback first so sensors are immediately ready
            phoneFallbackProvider.initialize(context)
            huaweiProvider.initialize(context)

            // Listen to Phone Fallback motion
            phoneFallbackProvider.accelerometerStream.collect { accel ->
                currentAccel = accel
                if (_isUsingPhoneFallback.value) {
                    _currentMotion.value = MotionReading(accel, currentGyro, isPhoneFallback = true)
                }
            }
        }

        scope.launch {
            phoneFallbackProvider.gyroscopeStream.collect { gyro ->
                currentGyro = gyro
                if (_isUsingPhoneFallback.value) {
                    _currentMotion.value = MotionReading(currentAccel, gyro, isPhoneFallback = true)
                }
            }
        }

        // Listen to Huawei streams
        scope.launch {
            huaweiProvider.heartRateStream.collect { hr ->
                if (!_isUsingPhoneFallback.value) {
                    _currentHeartRate.value = hr.bpm
                }
            }
        }

        scope.launch {
            huaweiProvider.accelerometerStream.collect { accel ->
                if (!_isUsingPhoneFallback.value) {
                    currentAccel = accel
                    _currentMotion.value = MotionReading(accel, currentGyro, isPhoneFallback = false)
                }
            }
        }

        // Set initial device to Phone Fallback
        _activeDevice.value = phoneFallbackProvider.connectedDevice.value
        _isUsingPhoneFallback.value = true
    }

    suspend fun scanAllDevices(context: Context): List<WearableDevice> {
        val results = mutableListOf<WearableDevice>()
        for (p in providers) {
            results.addAll(p.startScan(context))
        }
        _scannedDevices.value = results
        return results
    }

    suspend fun connectDevice(context: Context, device: WearableDevice): Boolean {
        return when (device.brand) {
            WearableBrand.HUAWEI -> {
                val success = huaweiProvider.connect(context, device.id)
                if (success) {
                    _activeDevice.value = huaweiProvider.connectedDevice.value
                    _isUsingPhoneFallback.value = false
                }
                success
            }
            WearableBrand.PHONE_FALLBACK -> {
                phoneFallbackProvider.connect(context, device.id)
                _activeDevice.value = phoneFallbackProvider.connectedDevice.value
                _isUsingPhoneFallback.value = true
                _currentHeartRate.value = null
                true
            }
            else -> false
        }
    }

    suspend fun disconnectDevice() {
        huaweiProvider.disconnect()
        samsungProvider.disconnect()
        garminProvider.disconnect()
        amazfitProvider.disconnect()

        // Fallback to phone sensors
        _isUsingPhoneFallback.value = true
        _activeDevice.value = phoneFallbackProvider.connectedDevice.value
        _currentHeartRate.value = null
    }
}

