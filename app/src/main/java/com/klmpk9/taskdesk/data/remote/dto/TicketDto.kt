package com.klmpk9.taskdesk.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TicketDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "brief") val brief: String,
    @Json(name = "driveLink") val driveLink: String?,
    @Json(name = "status") val status: String, // "Pending", "In Progress", "Done"
    @Json(name = "createdAt") val createdAt: Long
)
