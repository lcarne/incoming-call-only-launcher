package com.incomingcallonly.launcher.data.model

import com.google.gson.annotations.SerializedName

data class ContactExportDto(
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("photoBase64")
    val photoBase64: String?
)
