package com.ansh.sportsapp.presentation.my_gigs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.gig.GetMyRequestUseCase
import com.ansh.sportsapp.domain.usecase.gig.ManageRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyGigsViewModel @Inject constructor(
    private val getMyRequestUseCase: GetMyRequestUseCase,
    private val manageRequestUseCase: ManageRequestUseCase
) : ViewModel(){
    private val _state = MutableStateFlow(MyGigsState())
    val state : StateFlow<MyGigsState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MyGigsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadRequests()
    }

    private fun loadRequests() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getMyRequestUseCase()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            requests = result.data ?: emptyList()
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load requests"
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun processRequest(requestId: Long, isAccept: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = if (isAccept) {
                manageRequestUseCase.accept(requestId)
            } else {
                manageRequestUseCase.reject(requestId)
            }

            when (result) {
                is Resource.Success -> {
                    _uiEvent.emit(MyGigsUiEvent.ShowSnackbar(if (isAccept) "Request Accepted" else "Request Rejected"))
                    // Reload the list to remove the processed request
                    loadRequests()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.emit(MyGigsUiEvent.ShowSnackbar(result.message ?: "Action failed"))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onAccept(requestId: Long) {
        processRequest(requestId, isAccept = true)
    }

    fun onReject(requestId: Long) {
        processRequest(requestId, isAccept = false)
    }
}