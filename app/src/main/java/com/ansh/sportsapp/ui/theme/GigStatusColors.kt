package com.ansh.sportsapp.ui.theme

import androidx.compose.ui.graphics.Color
import com.ansh.sportsapp.domain.model.GigStatus

fun gigStatusColor(status: GigStatus): Color = when (status) {
    GigStatus.ACTIVE    -> SportGreen
    GigStatus.FULL      -> WarningAmber
    GigStatus.COMPLETED -> TertiaryIndigo
    GigStatus.EXPIRED   -> OnSurfaceHint
}

fun gigStatusContainer(status: GigStatus): Color = when (status) {
    GigStatus.ACTIVE    -> SportGreenContainer
    GigStatus.FULL      -> WarningContainer
    GigStatus.COMPLETED -> TertiaryContainer
    GigStatus.EXPIRED   -> Color(0x0FFFFFFF)
}