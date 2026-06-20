package com.ansh.sportsapp.presentation.create_gig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.remote.NominatimApi
import com.ansh.sportsapp.domain.usecase.gig.CreateGigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CreateGigViewModel @Inject constructor(
    private val useCase: CreateGigUseCase,
    private val nominatimApi: NominatimApi
) : ViewModel() {

    val availableSports: List<Pair<Long, String>> = listOf(
        1L to "Basketball",
        2L to "Soccer",
        3L to "Tennis",
        4L to "Volleyball",
        5L to "Cricket"
    )

    private val _state = MutableStateFlow(CreateGigState())
    val state: StateFlow<CreateGigState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CreateGigUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _locationQuery = MutableStateFlow("")
    private var reverseGeoJob: Job? = null

    init {
        viewModelScope.launch {
            _locationQuery
                .debounce(1000L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.length >= 3) searchLocation(query)
                    else _state.update { it.copy(locationSuggestions = emptyList()) }
                }
        }
    }

    fun onEvent(event: CreateGigEvent) {
        when (event) {
            is CreateGigEvent.EnteredSport -> _state.update { it.copy(sport = event.value) }
            is CreateGigEvent.LocationQueryChanged -> {
                _state.update { it.copy(locationQuery = event.query, locationSuggestions = emptyList()) }
                _locationQuery.value = event.query
            }
            is CreateGigEvent.SuggestionSelected -> {
                val lat = event.result.lat.toDoubleOrNull()
                val lng = event.result.lon.toDoubleOrNull()
                _state.update {
                    it.copy(
                        locationQuery = event.result.displayName,
                        locationDisplay = event.result.displayName,
                        locationSuggestions = emptyList(),
                        selectedLat = lat,
                        selectedLng = lng
                    )
                }
            }
            is CreateGigEvent.MarkerDragged -> {
                _state.update { it.copy(selectedLat = event.lat, selectedLng = event.lng) }
                reverseGeocode(event.lat, event.lng)
            }
            is CreateGigEvent.EnteredDate -> _state.update { it.copy(date = event.value) }
            is CreateGigEvent.EnteredTime -> _state.update { it.copy(time = event.value) }
            is CreateGigEvent.EnteredPlayers -> _state.update { it.copy(players = event.value) }
            is CreateGigEvent.Submit -> createGig()
        }
    }

    private fun searchLocation(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSearchingLocation = true) }
            try {
                val results = nominatimApi.search(query)
                _state.update { it.copy(locationSuggestions = results, isSearchingLocation = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isSearchingLocation = false, locationSuggestions = emptyList()) }
            }
        }
    }

    private fun reverseGeocode(lat: Double, lng: Double) {
        reverseGeoJob?.cancel()
        reverseGeoJob = viewModelScope.launch {
            try {
                val result = nominatimApi.reverse(lat, lng)
                _state.update { it.copy(locationDisplay = result.displayName, locationQuery = result.displayName) }
            } catch (e: Exception) {
                // keep existing display
            }
        }
    }

    private fun createGig() {
        viewModelScope.launch {
            val s = state.value
            if (s.selectedLat == null || s.selectedLng == null) {
                _uiEvent.emit(CreateGigUiEvent.ShowSnackbar("Please select a location on the map"))
                return@launch
            }
            _state.update { it.copy(isLoading = true) }

            when (val result = useCase(
                sport = s.sport,
                location = s.locationDisplay.ifBlank { s.locationQuery },
                latitude = s.selectedLat,
                longitude = s.selectedLng,
                date = s.date,
                time = s.time,
                playersNeeded = s.players
            )) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.emit(CreateGigUiEvent.GigCreated)
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.emit(CreateGigUiEvent.ShowSnackbar(result.message ?: "Failed"))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
