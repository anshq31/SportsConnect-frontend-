package com.ansh.sportsapp.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansh.sportsapp.common.Resource
import com.ansh.sportsapp.data.remote.dto.user.ReviewRequestDto
import com.ansh.sportsapp.domain.usecase.review.SubmitReviewUseCase
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
class ReviewViewModel @Inject constructor(
    private val submitReviewUseCase: SubmitReviewUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SubmitReviewState())

    val state : StateFlow<SubmitReviewState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SubmitReviewUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    fun submitReview(gigId: Long,participantId : Long){
        val rating = state.value.rating
        if (rating == 0){
            viewModelScope.launch {
                _uiEvent.emit(SubmitReviewUiEvent.ShowSnackbar("Please select a rating"))
            }
            return
        }


        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            val result = submitReviewUseCase(ReviewRequestDto(
                gigId = gigId,
                participantId = participantId,
                rating = rating,
                comment = state.value.comment))

            when(result) {
                is Resource.Success -> {
                    _state.update { it.copy(isSubmitting = false, success = true) }
                    _uiEvent.emit(SubmitReviewUiEvent.Success)
                }

                is Resource.Error -> {
                    _state.update { it.copy(isSubmitting = false, error = result.message) }
                    _uiEvent.emit(
                        SubmitReviewUiEvent.ShowSnackbar(
                            result.message ?: "Unable to submit "
                        )
                    )
                }
                else -> Unit
            }
        }
    }

    fun onRatingChange(rating: Int){
        _state.update { it.copy(rating = rating) }
    }

    fun onCommentChange(comment: String){
        _state.update { it.copy(comment = comment) }
    }

    fun resetState(){
        _state.value = SubmitReviewState()
    }
}