package com.klmpk9.taskdesk.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmpk9.taskdesk.data.remote.dto.TicketDto
import com.klmpk9.taskdesk.data.repository.TicketRepository
import com.klmpk9.taskdesk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

//ViewModel untuk Detail Screen.

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketRepository
) : ViewModel() {

    //Sealed interface untuk state UI Detail Screen.
    sealed interface UiState {
        data object Loading : UiState
        data class Success(val ticket: TicketDto) : UiState
        data class Error(val message: String) : UiState
    }

    // Ambil ticketId dari navigation argument
    private val ticketId: String = checkNotNull(savedStateHandle["ticketId"]) {
        "ticketId harus disediakan saat navigasi ke DetailScreen"
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    //    Pull refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    init {
        loadTicketDetail()
    }

    //Memuat detail tiket dari repository berdasarkan ticketId.
    private fun loadTicketDetail() {
        repository.getTicketById(ticketId)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = UiState.Loading
                    }
                    is Resource.Success -> {
                        _uiState.value = UiState.Success(resource.data)
                    }
                    is Resource.Error -> {
                        _uiState.value = UiState.Error(
                            resource.message ?: "Terjadi kesalahan tidak diketahui"
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    //    Pull to refresh
    fun refresh() {
        _isRefreshing.value = true
        repository.getTicketById(ticketId)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        _uiState.value = UiState.Success(resource.data)
                        _isRefreshing.value = false
                    }
                    is Resource.Error -> {
                        // Jangan hapus data yang sedang tampil, cukup kabari via snackbar
                        _refreshError.value = resource.message ?: "Gagal memuat ulang"
                        _isRefreshing.value = false
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun clearRefreshError() {
        _refreshError.value = null
    }

    //Retry function untuk tombol "Coba Lagi" di error state.
    fun retry() {
        loadTicketDetail()
    }
}