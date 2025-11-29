package com.basu.vaccineremainder.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    const val ROLE_PARENT = "parent"
    const val ROLE_PROVIDER = "provider"

    // --- FIX 1: Define keys for SharedPreferences ---
    private const val PREF_NAME = "user_session"
    private const val KEY_IS_LOGGED_IN = "logged_in"
    private const val KEY_USER_ID = "logged_in_user_id"
    private const val KEY_USER_ROLE = "logged_in_role"
    private const val KEY_USER_EMAIL = "logged_in_user_email"

    // --- Helper function to get SharedPreferences instance ---
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun login(context: Context, role: String, userId: Int, email: String) {
        val editor = getPreferences(context).edit()
        with(editor) {
            putBoolean(KEY_IS_LOGGED_IN, true)

            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_ROLE, role)
            putString(KEY_USER_EMAIL, email) // Now 'email' and the key are defined
            apply()
        }
    }

    // --- FIX 3: Correctly retrieve the user's email ---
    fun getParentEmail(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_EMAIL, null)
    }

    // This function logs the user out
    fun logout(context: Context) {
        val editor = getPreferences(context).edit()
        with(editor) {
            clear() // Removes all data from the shared preferences
            apply()
        }
    }

    // This function gets the ID of the currently logged-in user
    fun getCurrentUserId(context: Context): Int {
        val editor = getPreferences(context)
        // Return -1 if no user is logged in
        return editor.getInt(KEY_USER_ID, -1)
    }


    // This function gets the role of the currently logged-in user
    fun getLoggedInRole(context: Context): String? {
        val editor = getPreferences(context)
        return editor.getString(KEY_USER_ROLE, null)
    }

}
