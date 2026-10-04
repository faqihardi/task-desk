package com.klmpk9.taskdesk.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data object Create

@Serializable
data class Detail(
    val ticketId: String
)