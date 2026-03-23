package com.ansh.sportsapp.presentation.my_gigs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.gig.CreatedGigUseCase
import com.ansh.sportsapp.domain.usecase.gig.GetJoinedGigUseCase
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
    private val joinedGigUseCase: GetJoinedGigUseCase,
    private val createdGigUseCase: CreatedGigUseCase,
) : ViewModel(){
    private val _state = MutableStateFlow(MyGigsState())
    val state : StateFlow<MyGigsState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MyGigsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadCreatedGigs()
        loadJoinedGigs()
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
}