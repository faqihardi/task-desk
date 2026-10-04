package com.klmpk9.taskdesk.data.repository

import com.klmpk9.taskdesk.data.remote.api.TicketApi
import com.klmpk9.taskdesk.data.remote.dto.TicketDto
import com.klmpk9.taskdesk.data.remote.dto.TicketRequest
import com.klmpk9.taskdesk.util.DeviceIdentityManager
import com.klmpk9.taskdesk.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TicketRepoImpl @Inject constructor(
    private val api: TicketApi,
    private val identityManager: DeviceIdentityManager
) : TicketRepository {

    override fun getMyTickets(): Flow<Resource<List<TicketDto>>> = flow {
        emit(Resource.Loading)
        try {
            val requesterId = identityManager.getRequesterId()
            val response = api.getTickets(requesterId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Sort by createdAt descending (terbaru di atas)
                    val sorted = body.sortedByDescending { it.createdAt }
                    emit(Resource.Success(sorted))
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
            emit(Resource.Error("Terjadi kesalahan tak terduga", e))
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
            emit(Resource.Error("Terjadi kesalahan tak terduga", e))
        }
    }

    override suspend fun createTicket(request: TicketRequest): Resource<TicketDto> {
        return try {
            val response = api.createTicket(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Simpan nama & departemen untuk auto-fill berikutnya
                    identityManager.saveName(request.requesterName)
                    identityManager.saveDepartment(request.department)
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
            Resource.Error("Terjadi kesalahan tak terduga", e)
        }
    }
}