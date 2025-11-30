package com.basu.vaccineremainder.features.childprofile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.repository.AppRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AddChildViewModel(
    private val repository: AppRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun addChild(child: Child) {
        viewModelScope.launch {

            // 1️⃣ Get currently logged-in parent's email
            val parentEmail = auth.currentUser?.email

            if (parentEmail == null) {
                // No logged-in user – you can show a snackbar / log an error if you want
                // For now just return
                return@launch
            }

            // 2️⃣ Attach parentEmail to the child before saving
            val childWithParent = child.copy(
                parentEmail = parentEmail
            )
            // If your Child also has parentId, set it here too.

            // 3️⃣ Insert into Room
            val newChildId = repository.insertChild(childWithParent)

            // 4️⃣ Create a fully populated child with generated ID
            val childWithId = childWithParent.copy(childId = newChildId)

            // 5️⃣ Save to Firestore
            repository.saveChildToFirestore(childWithId)

            // 6️⃣ Generate schedule for this child
            repository.generateScheduleForChild(
                childWithId.childId,
                childWithId.dateOfBirth
            )
        }
    }
}

class AddChildViewModelFactory(
    private val repository: AppRepository,
    private val auth: FirebaseAuth
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddChildViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddChildViewModel(repository, auth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
