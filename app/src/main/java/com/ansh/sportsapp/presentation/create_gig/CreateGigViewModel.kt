package com.ansh.sportsapp.presentation.create_gig

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.gig.CreateGigUseCase
//import com.ansh.sportsapp.presentation.my_gigs.GigEvent
//import com.ansh.sportsapp.presentation.my_gigs.GigEventBus
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
class CreateGigViewModel @Inject constructor(
    private val useCase: CreateGigUseCase,
//    private val gigEventBus: GigEventBus
) : ViewModel(){

    val availableSports: List<Pair<Long, String>> = listOf(
        1L to "Basketball",
        2L to "Soccer",
        3L to "Tennis",
        4L to "Volleyball",
        5L to "Cricket"
    )

    private val _state = MutableStateFlow(CreateGigState())
    val state : StateFlow<CreateGigState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CreateGigUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: CreateGigEvent) {
        when (event) {
            is CreateGigEvent.EnteredSport -> _state.update { it.copy(sport = event.value) }
            is CreateGigEvent.EnteredLocation -> _state.update { it.copy(location = event.value) }
            is CreateGigEvent.EnteredDate -> _state.update { it.copy(date = event.value) }
            is CreateGigEvent.EnteredTime -> _state.update { it.copy(time = event.value) }
            is CreateGigEvent.EnteredPlayers -> _state.update { it.copy(players = event.value) }
            is CreateGigEvent.Submit -> createGig()
        }
    }

    private fun createGig() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = useCase(
                sport = state.value.sport,
                location = state.value.location,
                date = state.value.date,
                time = state.value.time,
                playersNeeded = state.value.players
            )

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false) }
//                    gigEventBus.emit(GigEvent.GigCreated)
                    _uiEvent.emit(CreateGigUiEvent.GigCreated)
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    Log.d("CREATE_GIG", "Error creating gig: ${result.message.toString()}")
                    _uiEvent.emit(CreateGigUiEvent.ShowSnackbar(result.message ?: "Failed"))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}