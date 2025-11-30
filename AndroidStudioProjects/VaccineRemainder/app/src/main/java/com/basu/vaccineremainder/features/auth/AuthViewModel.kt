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
                // First, try to sign in the user.
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // Success! Create a User object to pass to the UI.
                    val user = User(
                        userId = 0, // Your local User model might have an ID, we can ignore it for now
                        uid = firebaseUser.uid, // IMPORTANT: The Firebase UID
                        name = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: ""
                        // Do not handle password here
                    )
                    _loginResult.emit(user)
                } else {
                    _loginResult.emit(null)
                }

            } catch (e: Exception) {
                // If signIn fails, it's likely because the user doesn't exist.
                // So, we try to create a new user.
                try {
                    val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                    val firebaseUser = authResult.user

                    if (firebaseUser != null) {
                        // Success! A new user was created.
                        val user = User(
                            userId = 0,
                            uid = firebaseUser.uid,
                            name = "", // No name on signup, can be added later
                            email = firebaseUser.email ?: ""
                        )
                        _loginResult.emit(user)
                    } else {
                        _loginResult.emit(null)
                    }

                } catch (e2: Exception) {
                    // If both sign-in and creation fail, then it's a real error.
                    _loginResult.emit(null)
                }
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
                    // --- THIS IS THE NEW PART ---
                    // 2. Create a provider object to save in Firestore
                    val newProvider = hashMapOf(
                        "uid" to firebaseUser.uid, // The crucial link to the authenticated user
                        "name" to name,
                        "specialization" to "General" // You can set a default value
                    )

                    // 3. Save the new provider document to the 'providers' collection
                    Firebase.firestore.collection("providers")
                        .add(newProvider) // Use .add() to auto-generate the document ID
                        .await() // Wait for the operation to complete
                    // --- END OF NEW PART ---

                    // 4. Signal that the registration was a success
                    _registerResult.emit(true)

                } else {
                    _registerResult.emit(false)
                }
            } catch (e: Exception) {
                // This will fail if the email is already in use, password is weak, etc.
                _registerResult.emit(false)
            }
        }
    }


}
