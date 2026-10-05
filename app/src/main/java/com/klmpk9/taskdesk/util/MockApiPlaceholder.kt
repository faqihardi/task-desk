package com.klmpk9.taskdesk.util

fun String?.isMockApiPresent(placeholderPrefix: String): Boolean {
    if (this.isNullOrBlank()) return false
    val placeholder = Regex("(?i)^" + Regex.escape(placeholderPrefix) + "\\s*\\d+$")
    return !this.matches(placeholder)
}