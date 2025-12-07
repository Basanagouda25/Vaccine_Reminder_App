package com.basu.vaccineremainder.features.faq

data class FAQ(
    val question: String,
    val answer: String,
    val role: String = "both" // "parent", "provider", "both"
)

fun getFaqsForRole(role: String): List<FAQ> {
    val allFaqs = listOf(
        FAQ(
            question = "How do I register as a parent?",
            answer = "On the login screen, tap 'Register', fill your details and use a valid email. This email is used to receive notifications.",
            role = "parent"
        ),
        FAQ(
            question = "Why am I not getting notifications?",
            answer = "Make sure you have a stable internet connection, are logged in with the same email the provider uses, and notifications are enabled on your device.",
            role = "both"
        ),
        FAQ(
            question = "How does the provider send notifications?",
            answer = "Providers log in, select a child, enter title & message, and send. The parent linked to that child will receive the notification.",
            role = "provider"
        ),
        FAQ(
            question = "Can I use this app on multiple devices?",
            answer = "Yes. Just log in with the same email on any device. Data is synced via Firebase.",
            role = "both"
        ),
        FAQ(
            question = "What happens if I reinstall the app?",
            answer = "Your data is safe in Firebase. Just log in again with the same email to restore your data.",
            role = "both"
        ),
    )

    return allFaqs.filter { it.role == "both" || it.role == role }
}
