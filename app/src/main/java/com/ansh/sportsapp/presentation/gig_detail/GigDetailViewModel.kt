package com.ansh.sportsapp.presentation.gig_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.event.BlockEventBus
import com.ansh.sportsapp.domain.repository.UserRepository
import com.ansh.sportsapp.domain.usecase.gig.CompleteGigUseCase
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GigDetailViewModel @Inject constructor(
    private val getMyRequestUseCase: GetMyRequestUseCase,
    private val manageRequestUseCase: ManageRequestUseCase,
    private val requestJoinUseCase: RequestJoinUseCase,
    private val getGigByIdUseCase: GetGigByIdUseCase,
    private val completeGigUseCase: CompleteGigUseCase,
    private val userRepository: UserRepository,
    private val blockEventBus: BlockEventBus,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(GigDetailState())
    val state : StateFlow<GigDetailState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<GigDetailUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        savedStateHandle.get<Long>("gigId")?.let { id ->
            if (id != -1L) {
                loadGigThenRequests(id)
            }
        }

        viewModelScope.launch {
            blockEventBus.events.collectLatest { blockedId ->
                val gig = _state.value.gig ?: return@collectLatest
                val updatedParticipants = gig.acceptedParticipants.filter { it.id != blockedId }
                val updatedRequests = _state.value.requests.filter { it.requesterId != blockedId }
                _state.update {
                    it.copy(
                        gig = gig.copy(acceptedParticipants = updatedParticipants),
                        requests = updatedRequests
                    )
                }
            }
        }
    }

    private fun loadGigThenRequests(gigId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getGigByIdUseCase(gigId)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, gig = result.data) }
                    if (result.data?.isOwner == true) loadRequests()
                }
                is Resource.Error -> {
                    if (result.message == "403") {
                        _state.update { it.copy(isLoading = false, isBlockedAccess = true) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
                is Resource.Loading -> Unit
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
                    if (result.message == "403") {
                        _state.update { it.copy(isLoading = false, isBlockedAccess = true) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
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
                    val msg = if (result.message == "403") "Unable to join this gig"
                              else result.message ?: "Failed to join"
                    _uiEvent.emit(GigDetailUiEvent.ShowSnackBar(msg))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun blockParticipant(userId: Long, username: String) {
        val gigMasterUsername = state.value.gig?.gigMasterUsername ?: return
        viewModelScope.launch {
            _state.update { it.copy(blockingUserId = userId) }
            when (val result = userRepository.blockUser(userId, username)) {
                is Resource.Success -> {
                    blockEventBus.emit(userId)
                    if (username == gigMasterUsername) {
                        _uiEvent.emit(GigDetailUiEvent.NavigateBack)
                    } else {
                        _state.update { s ->
                            val currentGig = s.gig ?: return@update s
                            s.copy(
                                blockingUserId = null,
                                gig = currentGig.copy(
                                    acceptedParticipants = currentGig.acceptedParticipants.filter { it.id != userId }
                                ),
                                requests = s.requests.filter { it.requesterId != userId }
                            )
                        }
                        _uiEvent.emit(GigDetailUiEvent.ShowSnackBar("$username blocked"))
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(blockingUserId = null) }
                    _uiEvent.emit(GigDetailUiEvent.ShowSnackBar(result.message ?: "Failed to block user"))
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
            _state.update { it.copy(isRequestsLoading = true) }

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


    fun completeGig(){
        val gigId = state.value.gig?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when(val result = completeGigUseCase(gigId)){
                    is Resource.Success ->{
                        _state.update {
                            it.copy(
                                isLoading = false,
                                gig = result.data
                            )
                        }
//                        gigEventBus.emit(GigEvent.GigCompleted)
                        _uiEvent.emit(
                            GigDetailUiEvent.ShowSnackBar("Gig marked as completed")
                        )
                    }
                    is Resource.Error ->{
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.emit(
                            GigDetailUiEvent.ShowSnackBar(
                                result.message ?: "Failed to complete gig"
                            )
                        )
                    }
                    is Resource.Loading -> Unit
                }
            }catch (e: Exception){
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(GigDetailUiEvent.ShowSnackBar(e.message ?: "Failed to complete gig"))
            }
        }
    }
}