package com.basu.vaccineremainder.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    const val ROLE_PARENT = "parent"
    const val ROLE_PROVIDER = "provider"

    private const val PREF_NAME = "user_session"
    private const val KEY_IS_LOGGED_IN = "logged_in"
    // --- CHANGE 1: Use a generic KEY_ID that stores a String ---
    private const val KEY_ID = "logged_in_id"
    private const val KEY_USER_ROLE = "logged_in_role"
    private const val KEY_USER_EMAIL = "logged_in_user_email"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // --- CHANGE 2: Overload the login function ---
    // This is the original function for Parents, who still use an Int ID from the local Room DB.
    fun login(context: Context, role: String, userId: Int, email: String) {
        // We convert the Int to a String to store it consistently.
        login(context, role, userId.toString(), email)
    }

    // This is the NEW function for Providers, who use a String ID from Firestore.
    // The other login function calls this one.
    fun login(context: Context, role: String, userId: String, email: String) {
        val editor = getPreferences(context).edit()
        with(editor) {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_ID, userId) // <-- Now stores a String
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    fun getParentEmail(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_EMAIL, null)
    }

    fun logout(context: Context) {
        val editor = getPreferences(context).edit()
        with(editor) {
            clear()
            apply()
        }
    }

    // --- CHANGE 3: Update functions to return the correct ID type ---
    // This new function returns the stored ID as a String.
    // This is what you should use for Providers.
    fun getCurrentProviderId(context: Context): String? {
        return getPreferences(context).getString(KEY_ID, null)
    }

    // This function gets the ID and converts it to an Int for the Parent role.
    fun getCurrentUserId(context: Context): Int {
        val idString = getPreferences(context).getString(KEY_ID, "-1")
        // This will safely convert the stored ID to an Int.
        // It works for parents (e.g., "123" -> 123) and fails safely for providers (e.g., "aBcXyZ" -> -1).
        return idString?.toIntOrNull() ?: -1
    }

    fun getLoggedInRole(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_ROLE, null)
    }
}
