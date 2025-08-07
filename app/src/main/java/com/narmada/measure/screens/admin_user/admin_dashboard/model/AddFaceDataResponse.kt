package com.narmada.measure.screens.admin_user.admin_dashboard.model

import com.google.gson.annotations.SerializedName

class AddFaceDataResponse(

    @field:SerializedName("data")
    val data: AddFaceData? = null,

    @field:SerializedName("response")
    val response: String? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class AddFaceData(

    @field:SerializedName("face_image_path")
    val faceImagePath: String? = null,

)