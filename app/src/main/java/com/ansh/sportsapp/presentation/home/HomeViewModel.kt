package com.ansh.sportsapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.gig.GetActiveGigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val activeGigUseCase: GetActiveGigUseCase
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state : StateFlow<HomeState> = _state

    init {
        loadGigs()
    }

    fun loadGigs(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when(val result = activeGigUseCase()){
                is Resource.Success->{
                    _state.update {
                        it.copy(
                            isLoading = false,
                            gigs = result.data ?: emptyList()
                        )
                    }
                }
                is Resource.Error->{
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load gigs"
                        )
                    }
                }
                is Resource.Loading-> Unit
            }
        }
    }

    fun refresh(){
        loadGigs()
    }
}