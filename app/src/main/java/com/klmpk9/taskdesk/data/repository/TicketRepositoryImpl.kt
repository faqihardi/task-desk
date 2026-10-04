package com.klmpk9.taskdesk.data.repository

import com.klmpk9.taskdesk.data.remote.api.TicketApi
import com.klmpk9.taskdesk.data.remote.dto.TicketDto
import com.klmpk9.taskdesk.data.remote.dto.TicketRequest
import com.klmpk9.taskdesk.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TicketRepoImpl @Inject constructor(
    private val api: TicketApi
) : TicketRepository {

    override fun getTickets(): Flow<Resource<List<TicketDto>>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.getTickets()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(Resource.Success(body))
                } else {
                    emit(Resource.Error("Response body kosong"))
                }
            } else {
                emit(Resource.Error("Server error: HTTP ${response.code()}"))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Tidak ada koneksi internet", e))
        } catch (e: HttpException) {
            emit(Resource.Error("HTTP ${e.code()}: ${e.message()}", e))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error", e))
        }
    }

    override fun getTicketById(id: String): Flow<Resource<TicketDto>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.getTicketById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(Resource.Success(body))
                } else {
                    emit(Resource.Error("Tiket tidak ditemukan"))
                }
            } else {
                emit(Resource.Error("Server error: HTTP ${response.code()}"))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Tidak ada koneksi internet", e))
        } catch (e: HttpException) {
            emit(Resource.Error("HTTP ${e.code()}: ${e.message()}", e))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error", e))
        }
    }

    override suspend fun createTicket(request: TicketRequest): Resource<TicketDto> {
        return try {
            val response = api.createTicket(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error("Response body kosong")
                }
            } else {
                Resource.Error("Gagal membuat tiket: HTTP ${response.code()}")
            }
        } catch (e: IOException) {
            Resource.Error("Tidak ada koneksi internet", e)
        } catch (e: HttpException) {
            Resource.Error("HTTP ${e.code()}: ${e.message()}", e)
        } catch (e: Exception) {
            Resource.Error("Unexpected error", e)
        }
    }
}