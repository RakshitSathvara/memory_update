package com.narmada.measure.screens.admin_user.admin_dashboard.view_model

import com.narmada.measure.network.RetrofitService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AdminRepository constructor(private val retrofitService: RetrofitService) {
    suspend fun addSupervisorFace(
        supervisorId: RequestBody,
        faceData: RequestBody,
        supervisorImage: MultipartBody.Part,
        headers: HashMap<String, String>
    ) = retrofitService.addSupervisorFace(supervisorId, faceData, supervisorImage,headers)

    suspend fun deleteSupervisorFace(supervisorId: String,headers: HashMap<String, String>) =
        retrofitService.deleteSupervisorFace(supervisorId,headers)

}