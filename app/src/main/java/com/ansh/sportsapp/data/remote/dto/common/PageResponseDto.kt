package com.ansh.sportsapp.data.remote.dto.common

import com.google.gson.annotations.SerializedName

data class PageResponseDto<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("last") val last: Boolean,
    @SerializedName("size") val size: Int,
    @SerializedName("number") val number: Int
)