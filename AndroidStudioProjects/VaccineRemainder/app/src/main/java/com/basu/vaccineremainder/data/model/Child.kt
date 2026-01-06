package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true)
    val childId: Long = 0,
    val documentId: String = "",
    val parentId: Long=0L,
    val name: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val parentEmail: String = "",
    val providerId: String = "",
    val firestoreId: String = ""
)
