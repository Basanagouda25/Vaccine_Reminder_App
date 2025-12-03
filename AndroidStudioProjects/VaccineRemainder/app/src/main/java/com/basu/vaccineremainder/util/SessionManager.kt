package com.basu.vaccineremainder.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    const val ROLE_PARENT = "parent"
    const val ROLE_PROVIDER = "provider"

    // Single pref name used everywhere
    private const val PREF_NAME = "vaccine_session_prefs"

    private const val KEY_IS_LOGGED_IN = "logged_in"
    private const val KEY_ID = "logged_in_id"
    private const val KEY_USER_ROLE = "logged_in_role"
    private const val KEY_USER_EMAIL = "logged_in_user_email"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // -------- LOGIN (OVERLOADS) --------

    // Parent → stores local Room Int userId
    fun login(context: Context, role: String, userId: Int, email: String) {
        login(context, role, userId.toString(), email)
    }

    // Provider → stores Firestore/Firebase String id
    fun login(context: Context, role: String, userId: String, email: String) {
        prefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_ID, userId)
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    // -------- GETTERS --------

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_IS_LOGGED_IN, false)

    fun getParentEmail(context: Context): String? =
        prefs(context).getString(KEY_USER_EMAIL, null)

    // For parent: we expect this to be the local Int id (stored as String)
    fun getCurrentUserId(context: Context): Int =
        prefs(context).getString(KEY_ID, "-1")?.toIntOrNull() ?: -1

    // For provider: we expect this to be the String provider uid
    fun getCurrentProviderId(context: Context): String? =
        prefs(context).getString(KEY_ID, null)

    fun getLoggedInRole(context: Context): String? =
        prefs(context).getString(KEY_USER_ROLE, null)

    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
