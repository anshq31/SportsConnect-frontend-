package com.ansh.sportsapp.presentation.my_gigs


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.gig.CreatedGigUseCase
import com.ansh.sportsapp.domain.usecase.gig.GetJoinedGigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyGigsViewModel @Inject constructor(
    private val joinedGigUseCase: GetJoinedGigUseCase,
    private val createdGigUseCase: CreatedGigUseCase,
    private val gigEventBus: GigEventBus
) : ViewModel(){
    private val _state = MutableStateFlow(MyGigsState())
    val state : StateFlow<MyGigsState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MyGigsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadCreatedGigs()
        loadJoinedGigs()

        gigEventBus.events.
                onEach { event ->
                    when(event){
                        is GigEvent.GigCreated -> {
                            loadCreatedGigs()
                        }
                        is GigEvent.GigJoined -> {
                            loadJoinedGigs()
                        }
                        is GigEvent.GigCompleted -> {
                            loadCreatedGigs()
                            loadJoinedGigs()
                        }
                    }
                }.launchIn(viewModelScope)
    }

    fun loadJoinedGigs(){
        viewModelScope.launch {
            _state.update { it.copy(isJoinedGigsLoading = true, joinedGigError = null) }

            when(val result = joinedGigUseCase()){
                is Resource.Success->{
                    _state.update {
                        it.copy(
                            isJoinedGigsLoading = false,
                            joinedGigs = result.data ?: emptyList()
                        )
                    }
                }
                is Resource.Error->{
                    _state.update {
                        it.copy(
                            isJoinedGigsLoading = false,
                            joinedGigError = result.message ?: "Failed to load gigs"
                        )
                    }
                }
                is Resource.Loading-> Unit
            }
        }
    }

    fun loadCreatedGigs(){
        viewModelScope.launch {
            _state.update { it.copy(isCreatedGigsLoading = true, joinedGigError = null) }

            when(val result = createdGigUseCase()){
                is Resource.Success->{
                    _state.update {
                        it.copy(
                            isCreatedGigsLoading = false,
                            createdGig = result.data ?: emptyList()
                        )
                    }
                }
                is Resource.Error->{
                    _state.update {
                        it.copy(
                            isCreatedGigsLoading = false,
                            joinedGigError = result.message ?: "Failed to load gigs"
                        )
                    }
                }
                is Resource.Loading-> Unit
            }
        }
    }

    fun refresh(){
        loadJoinedGigs()
        loadCreatedGigs()
    }
}