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

    init {
        loadTickets()
    }

    fun loadTickets() {
        repository.getTickets()
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = UiState.Loading
                    }
                    is Resource.Success -> {
                        val tickets = resource.data
                        if (tickets.isEmpty()) {
                            _uiState.value = UiState.Empty
                        } else {
                            _uiState.value = UiState.Success(tickets)
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
     * Retry function jika error
     */
    fun retry() {
        loadTickets()
    }
}