package com.ansh.sportsapp.presentation.blocked_users

import com.ansh.sportsapp.data.local.BlockedUserEntry

data class BlockedUsersState(
    val blockedUsers: List<BlockedUserEntry> = emptyList(),
    val isLoading: Boolean = false,
    val unblockingId: Long? = null
)
