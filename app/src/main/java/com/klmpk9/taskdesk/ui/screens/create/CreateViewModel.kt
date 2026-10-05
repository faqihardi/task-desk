package com.klmpk9.taskdesk.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klmpk9.taskdesk.data.remote.dto.TicketRequest
import com.klmpk9.taskdesk.data.repository.TicketRepository
import com.klmpk9.taskdesk.util.Departments
import com.klmpk9.taskdesk.util.DeviceIdentityManager
import com.klmpk9.taskdesk.util.Resource
import com.klmpk9.taskdesk.util.TicketCodeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val repository: TicketRepository,
    private val identityManager: DeviceIdentityManager
) : ViewModel() {

    data class CreateUiState(
        val title: String = "",
        val brief: String = "",
        val driveLink: String = "",
        val requesterName: String = "",
        val department: String = "",
        val priority: String = "Medium",
        val isSubmitting: Boolean = false,
        val isSubmitSuccess: Boolean = false,
        val errorMessage: String? = null,
        val departments: List<String> = Departments.LIST,
        val priorities: List<String> = listOf("Low", "Medium", "High")
    )

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    init {
        val savedName = identityManager.getSavedName().orEmpty()
        val savedDept = identityManager.getSavedDepartment() ?: ""
        _uiState.update {
            it.copy(
                requesterName = savedName,
                department = savedDept
            )
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, errorMessage = null) }
    }

    fun onBriefChange(value: String) {
        _uiState.update { it.copy(brief = value, errorMessage = null) }
    }

    fun onDriveLinkChange(value: String) {
        _uiState.update { it.copy(driveLink = value, errorMessage = null) }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(requesterName = value, errorMessage = null) }
    }

    fun onDepartmentChange(value: String) {
        _uiState.update { it.copy(department = value, errorMessage = null) }
    }

    fun onPriorityChange(value: String) {
        _uiState.update { it.copy(priority = value, errorMessage = null) }
    }

    fun submitTicket() {
        val currentState = _uiState.value

        if (currentState.title.isBlank() || currentState.brief.isBlank() || currentState.requesterName.isBlank() ||
            currentState.department.isBlank()) return
        if (currentState.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            // Generate ticket code
            val ticketCode = TicketCodeGenerator.generate(currentState.department)

            val request = TicketRequest(
                ticketCode = ticketCode,
                title = currentState.title.trim(),
                brief = currentState.brief.trim(),
                driveLink = currentState.driveLink.trim().ifBlank { null },
                priority = currentState.priority,
                department = currentState.department,
                requesterId = identityManager.getRequesterId(),
                requesterName = currentState.requesterName.trim()
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