package com.klmpk9.taskdesk.data.repository

import com.klmpk9.taskdesk.data.remote.dto.TicketDto
import com.klmpk9.taskdesk.data.remote.dto.TicketRequest
import com.klmpk9.taskdesk.util.Resource
import kotlinx.coroutines.flow.Flow

interface TicketRepository {

    /**
     * Ambil daftar tiket milik user ini (filter by requesterId).
     */
    fun getMyTickets(): Flow<Resource<List<TicketDto>>>

    fun getTicketById(id: String): Flow<Resource<TicketDto>>

    suspend fun createTicket(request: TicketRequest): Resource<TicketDto>
}