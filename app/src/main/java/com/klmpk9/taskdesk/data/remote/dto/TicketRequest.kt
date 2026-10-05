package com.klmpk9.taskdesk.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TicketRequest(
    @Json(name = "ticketCode") val ticketCode: String,
    @Json(name = "title") val title: String,
    @Json(name = "brief") val brief: String,
    @Json(name = "driveLink") val driveLink: String?,
    @Json(name = "priority") val priority: String,
    @Json(name = "department") val department: String,
    @Json(name = "requesterId") val requesterId: String,
    @Json(name = "requesterName") val requesterName: String,

    @Json(name = "assignee") val assignee: String = "",
    @Json(name = "adminReply") val adminReply: String = "",
    @Json(name = "status") val status: String = "pending",
    @Json(name = "createdAt") val createdAt: Long = System.currentTimeMillis()
)