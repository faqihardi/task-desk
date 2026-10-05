package com.klmpk9.taskdesk.data.mock


import com.klmpk9.taskdesk.data.remote.api.TicketApi
import com.klmpk9.taskdesk.data.remote.dto.TicketDto
import com.klmpk9.taskdesk.data.remote.dto.TicketRequest
import kotlinx.coroutines.delay
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

/**
 * Mock implementation untuk testing
 * Simulasi delay jaringan 1-2 detik.
 */
class MockTicketApi : TicketApi {

    private val mockTickets = mutableListOf<TicketDto>()
//    private val mockTickets = mutableListOf(
//        TicketDto(
//            id = "1",
//            title = "Redesign Landing Page Medkominfo",
//            brief = "Perlu redesign halaman utama dengan fokus pada UX yang lebih intuitif dan mobile-first approach.",
//            driveLink = null,
//            status = "in_progress",
//            createdAt = System.currentTimeMillis() - 86400000 // 1 hari lalu
//        ),
//        TicketDto(
//            id = "2",
//            title = "Buat Poster Event Campus Expo",
//            brief = "Poster untuk event tahunan dengan tema 'Innovation for Future'.",
//            driveLink = null,
//            status = "pending",
//            createdAt = System.currentTimeMillis() - 172800000 // 2 hari lalu
//        ),
//        TicketDto(
//            id = "3",
//            title = "Video Profil Organisasi",
//            brief = "Video 2-3 menit yang menampilkan kegiatan dan pencapaian organisasi.",
//            driveLink = "https://drive.google.com/example",
//            status = "done",
//            createdAt = System.currentTimeMillis() - 259200000 // 3 hari lalu
//        )
//    )

    override suspend fun getTickets(): Response<List<TicketDto>> {
        delay(1500) // Simulasi network delay
        return Response.success(mockTickets)
    }

    override suspend fun createTicket(request: TicketRequest): Response<TicketDto> {
        delay(1000)
        val newTicket = TicketDto(
            id = (mockTickets.size + 1).toString(),
            ticketCode = request.ticketCode,
            title = request.title,
            brief = request.brief,
            driveLink = request.driveLink,
            status = "pending",
            priority = request.priority,
            department = request.department,
            requesterId = request.requesterId,
            requesterName = request.requesterName,
            assignee = null,
            createdAt = System.currentTimeMillis()
        )
        mockTickets.add(0, newTicket)
        return Response.success(newTicket)
    }

    override suspend fun getTicketById(id: String): Response<TicketDto> {
        delay(800)
        val ticket = mockTickets.find { it.id == id }
        return if (ticket != null) {
            Response.success(ticket)
        } else {
            Response.error(404, "Not found".toResponseBody())
        }
    }
}