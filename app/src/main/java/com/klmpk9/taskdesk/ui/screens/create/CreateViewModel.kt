package com.klmpk9.taskdesk.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmpk9.taskdesk.data.remote.dto.TicketRequest
import com.klmpk9.taskdesk.data.repository.TicketRepository
import com.klmpk9.taskdesk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val repository: TicketRepository
) : ViewModel() {

    data class CreateUiState(
        val title: String = "",
        val brief: String = "",
        val driveLink: String = "",
        val isSubmitting: Boolean = false,
        val isSubmitSuccess: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, errorMessage = null) }
    }

    fun onBriefChange(value: String) {
        _uiState.update { it.copy(brief = value, errorMessage = null) }
    }

    fun onDriveLinkChange(value: String) {
        _uiState.update { it.copy(driveLink = value, errorMessage = null) }
    }

    fun submitTicket() {
        val currentState = _uiState.value

        if (currentState.title.isBlank() || currentState.brief.isBlank()) return
        if (currentState.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val request = TicketRequest(
                title = currentState.title.trim(),
                brief = currentState.brief.trim(),
                driveLink = currentState.driveLink.trim().ifBlank { null }
            )

            when (val result = repository.createTicket(request)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isSubmitSuccess = true
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    fun onNavigationComplete() {
        _uiState.update { it.copy(isSubmitSuccess = false) }
    }
}