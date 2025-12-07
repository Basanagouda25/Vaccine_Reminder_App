package com.basu.vaccineremainder.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    const val ROLE_PARENT = "parent"
    const val ROLE_PROVIDER = "provider"

    private const val PREF_NAME = "vaccine_session_prefs"

    private const val KEY_IS_LOGGED_IN = "logged_in"
    private const val KEY_ID = "logged_in_id"
    private const val KEY_USER_ROLE = "logged_in_role"
    private const val KEY_USER_EMAIL = "logged_in_user_email"

    private const val KEY_PARENT_EMAIL = "parent_email"
    private const val KEY_PARENT_NAME = "parent_name"

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
        prefs(context).getString(KEY_PARENT_EMAIL, null)
            ?: prefs(context).getString(KEY_USER_EMAIL, null)

    fun getCurrentUserId(context: Context): Int =
        prefs(context).getString(KEY_ID, "-1")?.toIntOrNull() ?: -1

    fun getCurrentProviderId(context: Context): String? =
        prefs(context).getString(KEY_ID, null)

    fun getLoggedInRole(context: Context): String? =
        prefs(context).getString(KEY_USER_ROLE, null)

    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun saveParentEmail(context: Context, email: String) {
        prefs(context).edit().putString(KEY_PARENT_EMAIL, email).apply()
    }

    fun saveParentName(context: Context, name: String) {
        prefs(context).edit().putString(KEY_PARENT_NAME, name).apply()
    }

    fun getParentName(context: Context): String? {
        return prefs(context).getString(KEY_PARENT_NAME, null)
    }


}
