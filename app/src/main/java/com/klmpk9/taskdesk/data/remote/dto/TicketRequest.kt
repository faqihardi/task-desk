package com.klmpk9.taskdesk.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TicketRequest(
    @Json(name = "title") val title: String,
    @Json(name = "brief") val brief: String,
    @Json(name = "driveLink") val driveLink: String?
)