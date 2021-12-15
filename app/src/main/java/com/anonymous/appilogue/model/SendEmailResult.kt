package com.anonymous.appilogue.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendEmailResult(
    @Json(name = "isSend")
    val isSend: Boolean,
    @Json(name = "isUserExist")
    val isUserExist: Boolean
)
