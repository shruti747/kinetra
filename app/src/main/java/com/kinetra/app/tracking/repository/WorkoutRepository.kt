package com.kinetra.app.tracking.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kinetra.app.data.SessionManager
import com.kinetra.app.tracking.model.WorkoutSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Local persistent repository for workout records, strictly scoped per authenticated user ID.
 *
 * - Each authenticated user gets their own local history key (`saved_workouts_user_<id>`).
 * - Logging out clears only in-memory state without deleting persisted per-user records.
 * - Switching accounts loads only that account's own workout history.
 */
object WorkoutRepository {

    private const val PREFS_NAME = "kinetra_workouts_prefs"
    private const val KEY_PREFIX_USER_WORKOUTS = "saved_workouts_user_"

    private val gson = Gson()
    private var prefs: SharedPreferences? = null
    private var activeUserId: Int = -1

    private val _workouts = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val workouts: StateFlow<List<WorkoutSession>> = _workouts.asStateFlow()

    fun initialize(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentId = SessionManager.getCurrentUserId()
        setActiveUser(currentId)
    }

    fun setActiveUser(userId: Int) {
        activeUserId = userId
        if (userId <= 0) {
            _workouts.value = emptyList()
            return
        }
        loadWorkoutsForUser(userId)
    }

    fun clearActiveUserCache() {
        activeUserId = -1
        _workouts.value = emptyList()
    }

    private fun loadWorkoutsForUser(userId: Int) {
        val json = prefs?.getString("$KEY_PREFIX_USER_WORKOUTS$userId", null)
        if (!json.isNullOrBlank()) {
            try {
                val type = object : TypeToken<List<WorkoutSession>>() {}.type
                val list: List<WorkoutSession> = gson.fromJson(json, type)
                _workouts.value = list
                return
            } catch (_: Exception) {}
        }
        _workouts.value = emptyList()
    }

    fun saveWorkout(session: WorkoutSession) {
        val userId = if (activeUserId > 0) activeUserId else SessionManager.getCurrentUserId()
        if (userId <= 0) return

        val existing = _workouts.value.filterNot { it.id == session.id }
        val updated = listOf(session) + existing
        _workouts.value = updated
        saveToPrefs(userId, updated)
    }

    private fun saveToPrefs(userId: Int, list: List<WorkoutSession>) {
        val json = gson.toJson(list)
        prefs?.edit()?.putString("$KEY_PREFIX_USER_WORKOUTS$userId", json)?.apply()
    }
}
