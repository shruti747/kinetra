package com.kinetra.app.ui.devices

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetra.app.wearable.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for smartwatch scanning, pairing prerequisites, and hardware status.
 */
class ConnectedDevicesViewModel : ViewModel() {

    val activeDevice: StateFlow<WearableDevice?> = WearableManager.activeDevice
    val isUsingPhoneFallback: StateFlow<Boolean> = WearableManager.isUsingPhoneFallback
    val scannedDevices: StateFlow<List<WearableDevice>> = WearableManager.scannedDevices

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _huaweiAvailability = MutableStateFlow<ProviderAvailability?>(null)
    val huaweiAvailability: StateFlow<ProviderAvailability?> = _huaweiAvailability.asStateFlow()

    fun checkPrerequisites(context: Context) {
        _huaweiAvailability.value = WearableManager.huaweiProvider.checkAvailability(context)
    }

    fun scanDevices(context: Context) {
        viewModelScope.launch {
            _isScanning.value = true
            WearableManager.scanAllDevices(context)
            _isScanning.value = false
        }
    }

    fun connectDevice(context: Context, device: WearableDevice) {
        viewModelScope.launch {
            WearableManager.connectDevice(context, device)
        }
    }

    fun disconnectDevice() {
        viewModelScope.launch {
            WearableManager.disconnectDevice()
        }
    }
}

