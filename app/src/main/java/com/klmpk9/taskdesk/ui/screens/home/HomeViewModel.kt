package com.klmpk9.taskdesk.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmpk9.taskdesk.data.remote.dto.TicketDto
import com.klmpk9.taskdesk.data.repository.TicketRepository
import com.klmpk9.taskdesk.ui.components.TicketStatus
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

    // Error saat refresh (snackbar)
    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    //    Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    // === Status Filter State ===
    private val _statusFilter = MutableStateFlow<TicketStatus?>(null)  // null = Semua
    val statusFilter: StateFlow<TicketStatus?> = _statusFilter.asStateFlow()

    fun onStatusFilterChange(status: TicketStatus?) {
        _statusFilter.value = status
    }

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
     * filtering
     */
    fun filterTickets(
        tickets: List<TicketDto>,
        query: String,
        status: TicketStatus?
    ): List<TicketDto> {
        return tickets.filter { ticket ->
            val matchesQuery = query.isBlank() ||
                    ticket.title.contains(query, ignoreCase = true) ||
                    ticket.ticketCode.contains(query, ignoreCase = true) ||
                    ticket.brief.contains(query, ignoreCase = true) ||
                    ticket.requesterName.contains(query, ignoreCase = true)

            val matchesStatus = status == null ||
                    TicketStatus.fromString(ticket.status) == status

            matchesQuery && matchesStatus
        }
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