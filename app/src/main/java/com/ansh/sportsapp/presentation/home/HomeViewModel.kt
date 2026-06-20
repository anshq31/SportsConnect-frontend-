package com.ansh.sportsapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.event.BlockEventBus
import com.ansh.sportsapp.domain.repository.LocationRepository
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
    private val activeGigUseCase: GetActiveGigUseCase,
    private val locationRepository: LocationRepository,
    private val blockEventBus: BlockEventBus
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    private val _sportQuery = MutableStateFlow("")
    private val _nearMe = MutableStateFlow(false)
    private val _radiusKm = MutableStateFlow(15)

    private var loadGigJob: Job? = null

    init {
        _state.update { it.copy(hasLocationPermission = locationRepository.hasLocationPermission()) }

        viewModelScope.launch {
            combine(_sportQuery, _nearMe, _radiusKm) { sport, near, radius ->
                Triple(sport, near, radius)
            }
                .debounce(400L)
                .distinctUntilChanged()
                .collectLatest { (sport, near, radius) ->
                    val loc = if (near) _state.value.userLocation else null
                    loadGigs(sport, loc?.lat, loc?.lng, if (near) radius else null)
                }
        }

        viewModelScope.launch {
            blockEventBus.events.collectLatest { refresh() }
        }
    }

    private fun loadGigs(sport: String, lat: Double?, lng: Double?, radiusKm: Int?) {
        loadGigJob?.cancel()
        loadGigJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = activeGigUseCase(
                sport = sport.ifBlank { null },
                lat = lat,
                lng = lng,
                radiusKm = radiusKm
            )) {
                is Resource.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        gigs = (result.data ?: emptyList()).filter { gig -> !gig.isOwner && !gig.isParticipant }
                    )
                }
                is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message ?: "Failed to load gigs") }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onSportQueryChange(query: String) {
        _state.update { it.copy(sportQuery = query) }
        _sportQuery.value = query
    }

    fun onPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasLocationPermission = granted) }
        if (granted) loadLocation()
    }

    fun toggleNearMe() {
        val current = _state.value.nearMeActive
        if (!current && _state.value.userLocation == null && _state.value.hasLocationPermission) {
            loadLocation()
        }
        _state.update { it.copy(nearMeActive = !current) }
        _nearMe.value = !current
    }

    fun setRadius(km: Int) {
        _state.update { it.copy(radiusKm = km) }
        _radiusKm.value = km
    }

    private fun loadLocation() {
        viewModelScope.launch {
            val loc = locationRepository.getCurrentLocation()
            _state.update { it.copy(userLocation = loc) }
            if (loc != null && _state.value.nearMeActive) {
                loadGigs(_sportQuery.value, loc.lat, loc.lng, _radiusKm.value)
            }
        }
    }

    fun clearFilters() {
        _state.update { it.copy(sportQuery = "", nearMeActive = false, radiusKm = 15) }
        _sportQuery.value = ""
        _nearMe.value = false
        _radiusKm.value = 15
    }

    fun refresh() {
        val s = _state.value
        val loc = if (s.nearMeActive) s.userLocation else null
        loadGigs(s.sportQuery, loc?.lat, loc?.lng, if (s.nearMeActive) s.radiusKm else null)
    }
}
