package com.basu.vaccineremainder.features.reports

data class VaccineEntry(
    val name: String,
    val dateGiven: String?,
    val dueDate: String?,
    val status: String // "Completed" / "Pending"
)

data class ChildReport(
    val childName: String,
    val parentEmail: String,
    val dob: String,
    val vaccines: List<VaccineEntry>
)
