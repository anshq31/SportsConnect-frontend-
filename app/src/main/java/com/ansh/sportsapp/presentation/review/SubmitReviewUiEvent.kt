package com.ansh.sportsapp.presentation.review

sealed class SubmitReviewUiEvent {
    object Success : SubmitReviewUiEvent()
    data class ShowSnackbar(val message: String): SubmitReviewUiEvent()
}