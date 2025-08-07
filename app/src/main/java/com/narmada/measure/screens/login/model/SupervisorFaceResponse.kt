package com.narmada.measure.screens.login.model

import com.google.gson.annotations.SerializedName

data class SupervisorFaceResponse(
    @field:SerializedName("response")
    val response: String,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val data: SupervisorFace?,
)

data class SupervisorFace(
    @field:SerializedName("full_name")
    val fullName: String?,
    @field:SerializedName("face_image_path")
    val faceImagePath: String?,
    @field:SerializedName("face_data")
    val faceData: String?,
)
