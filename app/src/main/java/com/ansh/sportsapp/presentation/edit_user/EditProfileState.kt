package com.ansh.sportsapp.presentation.edit_user

data class EditProfileState (
    val experience: String = "",
    val selectedSkillIds: Set<Long> = emptySet(),
    val isSaving: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)