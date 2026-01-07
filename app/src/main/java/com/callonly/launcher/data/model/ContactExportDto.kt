package com.callonly.launcher.data.model

data class ContactExportDto(
    val name: String,
    val phoneNumber: String,
    val photoBase64: String?,
    val isFavorite: Boolean
)
