package com.basu.vaccineremainder.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    /* ================= STATE FLOWS ================= */

    /* ================= STATE FLOWS ================= */

    private val _loginResult = MutableSharedFlow<User?>(replay = 1)
    val loginResult = _loginResult.asSharedFlow()

    private val _otpSent = MutableSharedFlow<Boolean>(replay = 0)
    val otpSent = _otpSent.asSharedFlow()


    // --- ADD THIS BLOCK ---
    private val _registerResult = MutableSharedFlow<Boolean>()
    val registerResult = _registerResult.asSharedFlow()
    // --- END BLOCK ---

    private var verificationId: String? = null


    /* ================= EMAIL LOGIN ================= */

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.length < 6) {
            emitLogin(null)
            return
        }

        viewModelScope.launch {
            try {
                val result = auth
                    .signInWithEmailAndPassword(email.trim(), password)
                    .await()

                val user = result.user
                emitLogin(user?.toUser())

            } catch (e: Exception) {
                emitLogin(null)
            }
        }
    }

    /* ================= PHONE OTP: SEND ================= */

    fun sendOtp(
        phoneNumber: String,
        activity: android.app.Activity
    ) {
        if (phoneNumber.length < 10) {
            viewModelScope.launch { _otpSent.emit(false) }
            return
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // DO NOTHING
                }


                override fun onVerificationFailed(e: FirebaseException) {
                    viewModelScope.launch { _otpSent.emit(false) }
                }

                override fun onCodeSent(
                    verifId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = verifId
                    viewModelScope.launch { _otpSent.emit(true) }
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /* ================= PHONE OTP: VERIFY ================= */

    fun verifyOtp(code: String) {
        val id = verificationId ?: return

        val credential = PhoneAuthProvider.getCredential(id, code)
        signInWithPhoneCredential(credential)
    }

    /* ================= PHONE OTP: VERIFY ================= */


    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                // Step 1: Sign in to get the Firebase user
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // Step 2: Use the UID to fetch the complete user document from Firestore
                    val userDocument = Firebase.firestore.collection("users")
                        .document(firebaseUser.uid)
                        .get()
                        .await()

                    if (userDocument.exists()) {
                        // Step 3: Create a complete User object from the Firestore document
                        val completeUser = User(
                            userId = 0,
                            uid = userDocument.getString("uid") ?: "",
                            name = userDocument.getString("name") ?: "",
                            email = userDocument.getString("email") ?: ""
                        )
                        // Step 4: CORRECTED - Use the shared emitLogin function
                        emitLogin(completeUser)
                    } else {
                        // Fallback: If no Firestore doc, emit the incomplete user
                        emitLogin(firebaseUser.toUser())
                    }
                } else {
                    // Firebase user was null
                    emitLogin(null)
                }

            } catch (e: Exception) {
                // If any step fails, emit null
                emitLogin(null)
            }
        }
    }



    /* ================= REGISTER ================= */

    fun registerUser(name: String, email: String, password: String, phoneNumber: String) {
        // Use a single coroutine for the entire operation
        viewModelScope.launch {
            // 1. Validate inputs first
            if (name.isBlank() || email.isBlank() || phoneNumber.length < 10 || password.length < 6) {
                _registerResult.emit(false) // Emit failure for invalid input
                return@launch // Exit this coroutine
            }

            try {
                // 2. Create the user with email and password
                val result = auth
                    .createUserWithEmailAndPassword(email.trim(), password)
                    .await()

                val user = result.user
                    ?: throw IllegalStateException("Firebase user is null after creation")

                // 3. Update the user's profile with their name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
                user.updateProfile(profileUpdates).await()

                // 4. Save all user details (including phone number) to Firestore
                Firebase.firestore.collection("users")
                    .document(user.uid)
                    .set(
                        mapOf(
                            "uid" to user.uid,
                            "name" to name.trim(),
                            "email" to email.trim(),
                            "phone" to phoneNumber.trim() // <-- Save the phone number here
                        )
                    ).await()

                // 5. If all steps succeed, emit true
                _registerResult.emit(true)

            } catch (e: Exception) {
                // 6. If any step fails (e.g., email already exists), emit false
                _registerResult.emit(false)
            }
        }
    }


    fun logout() {
        auth.signOut()
    }

    /* ================= HELPERS ================= */

    private fun FirebaseUser.toUser(): User {
        return User(
            userId = 0,
            uid = uid,
            name = displayName ?: "",
            email = email ?: ""
        )
    }

    private fun emitLogin(user: User?) {
        viewModelScope.launch {
            _loginResult.emit(user)
        }
    }


}
