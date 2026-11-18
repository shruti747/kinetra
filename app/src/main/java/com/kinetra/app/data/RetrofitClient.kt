package com.kinetra.app.data

import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Physical devices connect over LAN; Android emulators route host localhost via 10.0.2.2
    private const val PHYSICAL_BASE_URL = "http://172.23.49.248/kinetra_pm/api/"
    private const val EMULATOR_BASE_URL = "http://10.0.2.2/kinetra_pm/api/"

    private fun isRunningOnEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.PRODUCT.contains("sdk_gphone")
            || Build.PRODUCT.contains("emulator")
    }

    val activeBaseUrl: String
        get() = if (isRunningOnEmulator()) EMULATOR_BASE_URL else PHYSICAL_BASE_URL

    /**
     * Attaches the device's per-installation Bearer token to every authenticated API request.
     * Never logs passwords or authentication tokens.
     */
    private val authHeaderInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = SessionManager.getAuthToken()
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("X-Auth-Token", token)
                .build()
        } else {
            original
        }
        chain.proceed(request)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authHeaderInterceptor)
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(activeBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val kinetraApi: KinetraApi by lazy {
        retrofit.create(KinetraApi::class.java)
    }
}
