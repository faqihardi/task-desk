package com.klmpk9.taskdesk.ui.screens.home

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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TicketRepository
) : ViewModel() {
    sealed interface UiState {
        data object Loading : UiState
        data class Success(val tickets: List<TicketDto>) : UiState
        data class Error(val message: String) : UiState
        data object Empty : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // === Pull-to-Refresh State ===
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Error saat refresh (untuk snackbar)
    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    init {
        loadTickets()
    }

    fun loadTickets() {
        repository.getMyTickets()
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = UiState.Loading
                    }
                    is Resource.Success -> {
                        val tickets = resource.data
                        _uiState.value = if (tickets.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(tickets)
                        }
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

    /**
     * Pull-to-refresh.
     */
    fun refresh() {
        _isRefreshing.value = true
        repository.getMyTickets()
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Nothing
                    }
                    is Resource.Success -> {
                        val tickets = resource.data
                        _uiState.value = if (tickets.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(tickets)
                        }
                        _isRefreshing.value = false
                    }
                    is Resource.Error -> {
                        _refreshError.value = resource.message ?: "Gagal memuat data"
                        _isRefreshing.value = false
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Retry function jika error
     */
    fun retry() {
        loadTickets()
    }

    /**
     * Clear refresh error di snackbar.
     */
    fun clearRefreshError() {
        _refreshError.value = null
    }
}