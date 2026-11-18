package com.kinetra.app.data

import android.content.Context
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Represents the device-local authentication lifecycle state.
 */
sealed class AuthSessionState {
    /** Verifying a persisted session token stored on this device. */
    object Checking : AuthSessionState()

    /** No authenticated session on this device — must display Welcome / Login. */
    object Unauthenticated : AuthSessionState()

    /** Valid authenticated session for a specific user on this device. */
    data class Authenticated(
        val user: UserProfile,
        val token: String
    ) : AuthSessionState()
}

/**
 * Device-specific authentication & session manager.
 *
 * Security & multi-device isolation guarantees:
 * - Fresh installations ALWAYS start in [AuthSessionState.Unauthenticated].
 * - Never stores plain-text passwords.
 * - Never falls back to a hardcoded user ID, email, or name.
 * - Stores an opaque per-device Bearer token issued by the backend.
 * - Excluded from Android cloud backup & device-to-device transfer in AndroidManifest.xml.
 */
object SessionManager {
    const val AUTH_PREFS_NAME = "kinetra_device_auth_session"
    private const val LEGACY_USER_PREFS_NAME = "kinetra_user_prefs"

    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_TOKEN_EXPIRES_AT = "token_expires_at"
    private const val KEY_DEVICE_INSTANCE_ID = "device_instance_id"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_BIO = "user_bio"
    private const val KEY_USER_LOCATION = "user_location"
    private const val KEY_PREFERRED_SPORTS = "preferred_sports"
    private const val KEY_PROFILE_PHOTO = "profile_photo"
    private const val KEY_CREATED_AT = "created_at"

    private var appContext: Context? = null

    private val _authState = MutableStateFlow<AuthSessionState>(AuthSessionState.Unauthenticated)
    val authState: StateFlow<AuthSessionState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    @Volatile
    private var cachedToken: String? = null

    @Volatile
    private var cachedUserId: Int = -1

    /**
     * Synchronously inspects this device's local session store on app launch.
     * - If no valid local token & user exist, immediately emits [AuthSessionState.Unauthenticated].
     * - If a local session exists, enters [AuthSessionState.Checking] until validated.
     */
    fun initialize(context: Context) {
        val ctx = context.applicationContext
        appContext = ctx

        val prefs = ctx.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)

        // Ensure this specific installation has its own device instance identifier
        if (prefs.getString(KEY_DEVICE_INSTANCE_ID, null).isNullOrBlank()) {
            prefs.edit()
                .putString(KEY_DEVICE_INSTANCE_ID, UUID.randomUUID().toString())
                .apply()
        }

        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val token = prefs.getString(KEY_AUTH_TOKEN, null)?.trim()
        val userId = prefs.getInt(KEY_USER_ID, -1)
        val name = prefs.getString(KEY_USER_NAME, "")?.trim().orEmpty()
        val email = prefs.getString(KEY_USER_EMAIL, "")?.trim().orEmpty()

        if (!isLoggedIn || token.isNullOrBlank() || userId <= 0 || email.isBlank()) {
            // Fresh install or incomplete session — never fall back to any hardcoded account
            cachedToken = null
            cachedUserId = -1
            _currentUser.value = null
            _authState.value = AuthSessionState.Unauthenticated
            UserSession.syncFromSessionManager(null)
            return
        }

        val restoredProfile = UserProfile(
            id = userId,
            name = name,
            email = email,
            phone = prefs.getString(KEY_USER_PHONE, "").orEmpty(),
            bio = prefs.getString(KEY_USER_BIO, "").orEmpty(),
            location = prefs.getString(KEY_USER_LOCATION, "").orEmpty(),
            preferred_sports = prefs.getString(KEY_PREFERRED_SPORTS, "").orEmpty(),
            profile_photo = prefs.getString(KEY_PROFILE_PHOTO, "").orEmpty(),
            created_at = prefs.getString(KEY_CREATED_AT, "").orEmpty()
        )

        cachedToken = token
        cachedUserId = userId
        _currentUser.value = restoredProfile
        UserSession.syncFromSessionManager(restoredProfile)

        // Hold in Checking state until startup validation confirms token validity
        _authState.value = AuthSessionState.Checking
    }

    /**
     * Validates the persisted device token against the backend on app startup.
     * - 200 OK -> refreshes profile and emits [AuthSessionState.Authenticated].
     * - 401 Unauthorized -> clears invalid/expired session and redirects to Login.
     * - Network unreachable -> allows offline continuity ONLY for an already-persisted local session.
     */
    suspend fun validatePersistedSession(context: Context) {
        val token = cachedToken
        val cachedProfile = _currentUser.value

        if (token.isNullOrBlank() || cachedProfile == null || cachedProfile.id <= 0) {
            clearSession(context)
            return
        }

        val repository = AuthRepository()
        when (val result = repository.validateSession()) {
            is SessionValidationResult.Valid -> {
                val freshUser = result.user
                persistProfileFields(context, freshUser)
                cachedUserId = freshUser.id
                _currentUser.value = freshUser
                UserSession.syncFromSessionManager(freshUser)
                _authState.value = AuthSessionState.Authenticated(freshUser, token)
            }
            is SessionValidationResult.InvalidOrExpired -> {
                clearSession(context)
            }
            is SessionValidationResult.NetworkUnavailable -> {
                // Keep THIS device's previously authenticated user usable offline
                _authState.value = AuthSessionState.Authenticated(cachedProfile, token)
            }
        }
    }

    /**
     * Persists a newly authenticated user and per-device token after Login or Sign Up.
     */
    fun saveAuthenticatedSession(
        context: Context,
        user: UserProfile,
        token: String,
        expiresAt: String? = null
    ) {
        val ctx = context.applicationContext
        appContext = ctx

        val prefs = ctx.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_AUTH_TOKEN, token)
            .putString(KEY_TOKEN_EXPIRES_AT, expiresAt.orEmpty())
            .putInt(KEY_USER_ID, user.id)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_PHONE, user.phone.orEmpty())
            .putString(KEY_USER_BIO, user.bio.orEmpty())
            .putString(KEY_USER_LOCATION, user.location.orEmpty())
            .putString(KEY_PREFERRED_SPORTS, user.preferred_sports.orEmpty())
            .putString(KEY_PROFILE_PHOTO, user.profile_photo.orEmpty())
            .putString(KEY_CREATED_AT, user.created_at.orEmpty())
            .commit()

        cachedToken = token
        cachedUserId = user.id
        _currentUser.value = user
        UserSession.syncFromSessionManager(user)
        _authState.value = AuthSessionState.Authenticated(user, token)
    }

    /**
     * Updates cached profile fields for the currently authenticated user without changing the token.
     */
    fun updateCachedProfile(context: Context?, user: UserProfile) {
        val currentId = cachedUserId
        if (currentId <= 0 || user.id != currentId) return

        val ctx = context?.applicationContext ?: appContext
        if (ctx != null) {
            persistProfileFields(ctx, user)
        }
        _currentUser.value = user
        UserSession.syncFromSessionManager(user)

        val token = cachedToken
        if (!token.isNullOrBlank() && _authState.value is AuthSessionState.Authenticated) {
            _authState.value = AuthSessionState.Authenticated(user, token)
        }
    }

    private fun persistProfileFields(context: Context, user: UserProfile) {
        val prefs = context.applicationContext.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_USER_ID, user.id)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_PHONE, user.phone.orEmpty())
            .putString(KEY_USER_BIO, user.bio.orEmpty())
            .putString(KEY_USER_LOCATION, user.location.orEmpty())
            .putString(KEY_PREFERRED_SPORTS, user.preferred_sports.orEmpty())
            .putString(KEY_PROFILE_PHOTO, user.profile_photo.orEmpty())
            .putString(KEY_CREATED_AT, user.created_at.orEmpty())
            .apply()
    }

    /**
     * Clears ALL local authentication state, tokens, and cached user profile data on this device.
     * Does NOT touch server-side workout history or other devices' tokens.
     */
    fun clearSession(context: Context? = null) {
        val ctx = context?.applicationContext ?: appContext
        if (ctx != null) {
            val authPrefs = ctx.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)
            val preservedDeviceId = authPrefs.getString(KEY_DEVICE_INSTANCE_ID, null)
            authPrefs.edit()
                .clear()
                .apply {
                    if (!preservedDeviceId.isNullOrBlank()) {
                        putString(KEY_DEVICE_INSTANCE_ID, preservedDeviceId)
                    }
                }
                .commit()

            // Also wipe legacy shared preferences so old cached accounts can never resurface
            ctx.getSharedPreferences(LEGACY_USER_PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit()
        }

        cachedToken = null
        cachedUserId = -1
        _currentUser.value = null
        UserSession.syncFromSessionManager(null)
        _authState.value = AuthSessionState.Unauthenticated
    }

    fun getAuthToken(): String? = cachedToken

    fun getCurrentUserId(): Int = cachedUserId

    fun isAuthenticated(): Boolean =
        _authState.value is AuthSessionState.Authenticated &&
            cachedUserId > 0 &&
            !cachedToken.isNullOrBlank()

    fun getDeviceLabel(context: Context? = null): String {
        val ctx = context?.applicationContext ?: appContext
        val deviceIdShort = ctx
            ?.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)
            ?.getString(KEY_DEVICE_INSTANCE_ID, null)
            ?.take(8)
            ?: "device"
        return "${Build.MANUFACTURER} ${Build.MODEL} ($deviceIdShort)".trim().take(120)
    }
}

