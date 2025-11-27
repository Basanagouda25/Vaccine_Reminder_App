package com.basu.vaccineremainder.util

import android.content.Context

object SessionManager {

    const val ROLE_PARENT = "parent"
    const val ROLE_PROVIDER = "provider"

    // This function logs a user in by saving their role and ID
    fun login(context: Context, role: String, userId: Int) {
        val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("logged_in", true)
            putInt("logged_in_user_id", userId)
            putString("logged_in_role", role) // Save the role
            apply()
        }
    }

    // This function logs the user out
    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear() // Removes all data from the shared preferences
            apply()
        }
    }

    // This function gets the ID of the currently logged-in user
    fun getCurrentUserId(context: Context): Int {
        val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        // Return -1 if no user is logged in
        return sharedPref.getInt("logged_in_user_id", -1)
    }


    // This function gets the role of the currently logged-in user
    fun getLoggedInRole(context: Context): String? {
        val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return sharedPref.getString("logged_in_role", null)
    }

}
