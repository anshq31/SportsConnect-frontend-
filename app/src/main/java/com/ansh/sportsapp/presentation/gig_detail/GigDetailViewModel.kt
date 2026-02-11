package com.ansh.sportsapp.presentation.gig_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.gig.GetGigByIdUseCase
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
        savedStateHandle.get<String>("gigId")?.let { idString ->
            val id = idString.toLongOrNull()
            if (id != null) {
                loadGig(id)
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
                }
                is Resource.Error -> {
                    _state.update { it.copy(isJoinLoading = false) }
                    _uiEvent.emit(GigDetailUiEvent.ShowSnackBar(result.message ?: "Failed to join"))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}