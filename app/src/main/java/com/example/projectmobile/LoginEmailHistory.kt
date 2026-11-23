package com.example.projectmobile

import android.content.Context

object LoginEmailHistory {

    private const val PREF_NAME = "temanstudi_login_history"
    private const val KEY_EMAILS = "successful_login_emails"

    fun record(context: Context, email: String) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) return

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val existing = prefs.getStringSet(KEY_EMAILS, emptySet())
            ?.toList()
            ?: emptyList()

        val updated = linkedSetOf<String>()
        updated.add(cleanEmail)
        existing.forEach {
            if (it != cleanEmail) updated.add(it)
        }

        prefs.edit()
            .putStringSet(KEY_EMAILS, updated)
            .apply()
    }

    fun getEmails(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return prefs.getStringSet(KEY_EMAILS, emptySet())
            ?.toList()
            ?.sortedByDescending { it }
            ?: emptyList()
    }
}
