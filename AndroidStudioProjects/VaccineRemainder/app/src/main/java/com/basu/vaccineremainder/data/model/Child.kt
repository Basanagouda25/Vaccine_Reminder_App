package com.basu.vaccineremainder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true)
    val childId: Long = 0,
    @DocumentId
    val documentId: String = "",
    val parentId: Int=0,
    val name: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val parentEmail: String = "",
    val providerId: String = ""
)
