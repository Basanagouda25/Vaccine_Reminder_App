package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.User // Keep your User model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ktx.firestore // <-- ADD THIS IMPORT


class AuthViewModel : ViewModel() { // No longer needs AppRepository for login

    // Get the instance of Firebase Authentication
    private val auth: FirebaseAuth = Firebase.auth

    private val _loginResult = MutableSharedFlow<User?>()
    val loginResult = _loginResult.asSharedFlow()

    private val _registerResult = MutableSharedFlow<Boolean>() // Keeping this in case you have a separate register screen
    val registerResult = _registerResult.asSharedFlow()


    /**
     * This function now handles both SIGNING IN and SIGNING UP a user.
     * Firebase automatically creates a new user if the email doesn't exist.
     */
    fun loginUser(email: String, password: String) {
        // Basic validation before calling Firebase
        if (email.isBlank() || password.length < 6) {
            viewModelScope.launch {
                _loginResult.emit(null) // Emit null for invalid input
            }
            return
        }

        viewModelScope.launch {
            try {
                // ✅ ONLY sign in existing user
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val user = User(
                        userId = 0,
                        uid = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: ""
                    )
                    _loginResult.emit(user)
                } else {
                    _loginResult.emit(null)
                }

            } catch (e: Exception) {
                // ❌ Login failed (wrong email/password, etc.)
                _loginResult.emit(null)
            }
        }
    }


    // You can delete the old registerUser function if you want, as loginUser now handles it.
    // Or keep it if you have a separate registration flow.

    fun onLogout() {
        auth.signOut() // Sign the user out from Firebase
    }

    // --- ADD THIS ENTIRE FUNCTION TO YOUR AuthViewModel.kt FILE ---

    // In AuthViewModel.kt, REPLACE the old registerUser function with this one

    fun registerUser(name: String, email: String, password: String) {
        // Basic validation before calling Firebase
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            viewModelScope.launch {
                _registerResult.emit(false) // Emit false for invalid input
            }
            return
        }

        viewModelScope.launch {
            try {
                // 1. Create the user in Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // 2. Save basic profile to Firestore (PARENTS, not providers)
                    val newUser = hashMapOf(
                        "uid" to firebaseUser.uid,
                        "name" to name,
                        "email" to email
                    )

                    Firebase.firestore.collection("users")
                        .document(firebaseUser.uid)   // use uid as document id
                        .set(newUser)
                        .await()

                    _registerResult.emit(true)
                } else {
                    _registerResult.emit(false)
                }
            } catch (e: Exception) {
                // This fails if email already in use, weak password, etc.
                _registerResult.emit(false)
            }
        }
    }



}
