package com.kinetra.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Reactive athlete session facade backed by [SessionManager].
 *
 * Contains NO hardcoded user ID, email, or account defaults.
 * When unauthenticated, [currentUserId] is -1 and [userProfile] is empty.
 */
object UserSession {

    private val emptyProfile = UserProfile(
        id = -1,
        name = "",
        email = "",
        phone = "",
        bio = "",
        location = "",
        preferred_sports = "",
        profile_photo = ""
    )

    val currentUserId: Int
        get() = SessionManager.getCurrentUserId()

    val isLoggedIn: Boolean
        get() = SessionManager.isAuthenticated()

    private val _userProfile = MutableStateFlow(emptyProfile)
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun init(context: Context) {
        SessionManager.initialize(context)
    }

    internal fun syncFromSessionManager(profile: UserProfile?) {
        _userProfile.value = profile ?: emptyProfile
    }

    fun updateProfile(context: Context?, profile: UserProfile) {
        SessionManager.updateCachedProfile(context, profile)
    }

    fun clear(context: Context? = null) {
        SessionManager.clearSession(context)
    }
}
