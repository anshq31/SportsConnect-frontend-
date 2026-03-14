package com.ansh.sportsapp.presentation.review

data class SubmitReviewState (
    val isSubmitting: Boolean = false,
    val error : String? = null,
    val success : Boolean = false,
    val rating : Int = 0,
    val comment : String = ""
)