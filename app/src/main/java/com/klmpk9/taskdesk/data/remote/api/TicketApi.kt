package com.klmpk9.taskdesk.data.remote.api

import com.klmpk9.taskdesk.data.remote.dto.TicketDto
import com.klmpk9.taskdesk.data.remote.dto.TicketRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TicketApi {

    @GET("tickets")
    suspend fun getTickets(): Response<List<TicketDto>>

    @POST("tickets")
    suspend fun createTicket(
        @Body request: TicketRequest
    ): Response<TicketDto>

    @GET("tickets/{id}")
    suspend fun getTicketById(
        @Path("id") id: String
    ): Response<TicketDto>
}