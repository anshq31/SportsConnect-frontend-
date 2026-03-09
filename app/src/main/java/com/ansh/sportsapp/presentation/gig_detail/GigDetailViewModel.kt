package com.ansh.sportsapp.presentation.gig_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.gig.GetGigByIdUseCase
import com.ansh.sportsapp.domain.usecase.gig.GetMyRequestUseCase
import com.ansh.sportsapp.domain.usecase.gig.ManageRequestUseCase
import com.ansh.sportsapp.domain.usecase.gig.RequestJoinUseCase
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
class GigDetailViewModel @Inject constructor(
    private val getMyRequestUseCase: GetMyRequestUseCase,
    private val manageRequestUseCase: ManageRequestUseCase,
    private val requestJoinUseCase: RequestJoinUseCase,
    private val getGigByIdUseCase: GetGigByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    private val _state = MutableStateFlow(GigDetailState())
    val state : StateFlow<GigDetailState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<GigDetailUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        // Retrieve gigId from navigation arguments
        savedStateHandle.get<Long>("gigId")?.let { id ->
            if (id != -1L) {
                loadGigThenRequests(id)
            }
        }
    }

    private fun loadGigThenRequests(gigId : Long){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when(val result = getGigByIdUseCase(gigId)){
                is Resource.Success->{
                    _state.update { it.copy(isLoading = false, gig = result.data) }
                    if (result.data?.isOwner == true){
                        loadRequests()
                    }
                }
                is Resource.Error->{
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading-> Unit
            }
        }
    }

    private fun loadGig(gigId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getGigByIdUseCase(gigId)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, gig = result.data) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onJoinClicked() {
        val gigId = state.value.gig?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isJoinLoading = true) }
            when (val result = requestJoinUseCase(gigId)) {
                is Resource.Success -> {
                    _state.update { it.copy(isJoinLoading = false) }
                    _uiEvent.emit(GigDetailUiEvent.JoinSuccess)
                    loadGig(gigId)
                }
                is Resource.Error -> {
                    _state.update { it.copy(isJoinLoading = false) }
                    _uiEvent.emit(GigDetailUiEvent.ShowSnackBar(result.message ?: "Failed to join"))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun loadRequests() {
        viewModelScope.launch {
            _state.update { it.copy(isRequestsLoading = true, error = null) }
            when (val result = getMyRequestUseCase()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isRequestsLoading = false,
                            requests = result.data ?: emptyList()
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isRequestsLoading = false,
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
                    _uiEvent.emit(GigDetailUiEvent.ShowSnackBar(if (isAccept) "Request Accepted" else "Request Rejected"))
                    // Reload the list to remove the processed request
                    loadRequests()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.emit(GigDetailUiEvent.ShowSnackBar(result.message ?: "Action failed"))
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