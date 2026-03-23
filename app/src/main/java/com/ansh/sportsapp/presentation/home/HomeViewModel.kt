package com.ansh.sportsapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.gig.GetActiveGigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val activeGigUseCase: GetActiveGigUseCase
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state : StateFlow<HomeState> = _state

    private val _sportQuery = MutableStateFlow("")
    private val _locationQuery = MutableStateFlow("")

    private var loadGigJob: Job? = null

    init {
        viewModelScope.launch {
            combine(_sportQuery,_locationQuery){sport,location->
                sport to location
            }
                .debounce { 400L }
                .distinctUntilChanged()
                .collectLatest { (sport, location) ->
                    loadGigs(sport, location)
                }
        }
    }

    fun loadGigs(sport : String = "", location : String = ""){
        loadGigJob?.cancel()

        loadGigJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when(val result = activeGigUseCase(
                sport = sport,
                location = location
            )){
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

    fun onSportQueryChange(query: String) {
        _state.update { it.copy(sportQuery = query) }
        _sportQuery.value = query
    }

    fun onLocationQueryChange(query: String) {
        _state.update { it.copy(locationQuery = query) }
        _locationQuery.value = query
    }

    fun clearFilters() {
        _state.update { it.copy(sportQuery = "", locationQuery = "") }
        _sportQuery.value = ""
        _locationQuery.value = ""
    }
    fun refresh(){
        loadGigs()
    }
}