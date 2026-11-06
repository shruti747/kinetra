package com.kinetra.app.tracking.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kinetra.app.tracking.model.ActivityType
import com.kinetra.app.tracking.model.WorkoutSession
import com.kinetra.app.wearable.WearableBrand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Local persistent repository for workout records.
 *
 * Saves workout sessions locally and prepares them for future synchronization
 * with the Kinetra PHP/MySQL backend.
 */
object WorkoutRepository {

    private const val PREFS_NAME = "kinetra_workouts_prefs"
    private const val KEY_WORKOUTS = "saved_workouts_json"

    private val gson = Gson()
    private var prefs: SharedPreferences? = null

    private val _workouts = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val workouts: StateFlow<List<WorkoutSession>> = _workouts.asStateFlow()

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadWorkouts()
    }

    private fun loadWorkouts() {
        val json = prefs?.getString(KEY_WORKOUTS, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<WorkoutSession>>() {}.type
                val list: List<WorkoutSession> = gson.fromJson(json, type)
                _workouts.value = list
                return
            } catch (_: Exception) {}
        }

        // Seed with realistic athlete workouts if empty
        val seeded = listOf(
            WorkoutSession(
                id = "wk_101",
                activityType = ActivityType.RUNNING,
                startTimeMillis = System.currentTimeMillis() - 86400000L * 2,
                endTimeMillis = System.currentTimeMillis() - 86400000L * 2 + 1800000L,
                durationSeconds = 1845L,
                distanceMeters = 5230.0,
                averageSpeedKmh = 10.2f,
                averagePaceFormatted = "5'52\"",
                caloriesBurned = 368,
                averageHeartRate = 148,
                maxHeartRate = 168,
                wearableBrand = WearableBrand.HUAWEI,
                isSyncedToBackend = true
            ),
            WorkoutSession(
                id = "wk_102",
                activityType = ActivityType.CYCLING,
                startTimeMillis = System.currentTimeMillis() - 86400000L * 4,
                endTimeMillis = System.currentTimeMillis() - 86400000L * 4 + 3200000L,
                durationSeconds = 3120L,
                distanceMeters = 15400.0,
                averageSpeedKmh = 17.8f,
                averagePaceFormatted = "3'22\"",
                caloriesBurned = 520,
                averageHeartRate = 136,
                maxHeartRate = 158,
                wearableBrand = WearableBrand.PHONE_FALLBACK,
                isSyncedToBackend = true
            ),
            WorkoutSession(
                id = "wk_103",
                activityType = ActivityType.WALKING,
                startTimeMillis = System.currentTimeMillis() - 86400000L * 6,
                endTimeMillis = System.currentTimeMillis() - 86400000L * 6 + 2400000L,
                durationSeconds = 2400L,
                distanceMeters = 3100.0,
                averageSpeedKmh = 4.6f,
                averagePaceFormatted = "12'54\"",
                caloriesBurned = 165,
                averageHeartRate = 98,
                maxHeartRate = 112,
                wearableBrand = WearableBrand.PHONE_FALLBACK,
                isSyncedToBackend = true
            )
        )
        _workouts.value = seeded
        saveToPrefs(seeded)
    }

    fun saveWorkout(session: WorkoutSession) {
        val updated = listOf(session) + _workouts.value
        _workouts.value = updated
        saveToPrefs(updated)
    }

    private fun saveToPrefs(list: List<WorkoutSession>) {
        val json = gson.toJson(list)
        prefs?.edit()?.putString(KEY_WORKOUTS, json)?.apply()
    }
}

