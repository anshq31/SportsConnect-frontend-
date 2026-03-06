package com.ansh.sportsapp.presentation.edit_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.domain.usecase.user.GetMyProfileUseCase
import com.ansh.sportsapp.domain.usecase.user.UpdateMyProfileUseCase
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
class EditProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateMyProfileUseCase: UpdateMyProfileUseCase
) : ViewModel(){
    private val _state = MutableStateFlow(EditProfileState())
    val state : StateFlow<EditProfileState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EditProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val availableSkills: List<Pair<Long, String>> = listOf(
        1L to "Basketball",
        2L to "Soccer",
        3L to "Tennis",
        4L to "Volleyball",
        5L to "Cricket"
    )

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when(val result = getMyProfileUseCase()){
                is Resource.Success->{
                    val profile = result.data!!

                    val currentSkillIds = profile.skills.mapNotNull { skillName->
                        availableSkills.find { it.second == skillName }?.first
                    }.toSet()

                    _state.update {
                        it.copy(
                        experience = profile.experience,
                        selectedSkillIds = currentSkillIds,
                        isLoading = false,
                        error = null
                        )
                    }
                }
                is Resource.Error->{
                    _state.update { it.copy(isLoading = false, error =  result.message ?: "Failed to load profile") }
                }
                is Resource.Loading-> Unit
            }
        }
    }

    fun onExperienceChange(value: String){
        _state.update { it.copy(experience = value) }
    }

    fun onSkillToggle(skillId: Long){
        _state.update {
            val newSet = it.selectedSkillIds.toMutableSet()
            if (newSet.contains(skillId)){
                newSet.remove(skillId)
            }else{
                newSet.add(skillId)
            }
            it.copy(selectedSkillIds = newSet)
        }
    }

    fun saveProfile(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentState = _state.value
            when(val result = updateMyProfileUseCase(
                experience = currentState.experience,
                skillIds = currentState.selectedSkillIds
            )){
                is Resource.Success->{
                    _state.update { it.copy(isLoading = false, error = null) }
                    _uiEvent.emit(EditProfileUiEvent.SaveSuccess)
                }
                is Resource.Error->{
                    _state.update { it.copy(isLoading = false, error = result.message ?: "Failed to update profile") }
                }
                is Resource.Loading-> Unit
            }
        }
    }
}