package com.ansh.sportsapp.presentation.gig_detail

import com.ansh.sportsapp.domain.model.Gig
import com.ansh.sportsapp.domain.model.GigRequest

data class GigDetailState (
    val gig: Gig? = null,
    val isLoading: Boolean = false,
    val isJoinLoading: Boolean = false,
    val error: String? = null,
    val requests: List<GigRequest> = emptyList()
){
    val isOwner : Boolean
        get() = gig?.isOwner ?: false

    val isParticipant: Boolean
        get() = gig?.isParticipant ?: false

    val hasPendingRequest: Boolean
        get() = gig?.requestStatus == "PENDING"

    val isRejected: Boolean
        get() = gig?.requestStatus == "REJECTED"

    val buttonState: JoinButtonState
        get() = when {
            isOwner -> JoinButtonState.HIDDEN
            isParticipant -> JoinButtonState.JOINED
            hasPendingRequest -> JoinButtonState.PENDING
            isRejected -> JoinButtonState.REJECTED
            else -> JoinButtonState.CAN_JOIN
        }
}

enum class JoinButtonState {
    HIDDEN,     // User is gig master - hide button
    JOINED,     // User is already a participant
    PENDING,    // User has a pending request
    REJECTED,   // User's request was rejected
    CAN_JOIN    // User can join
}