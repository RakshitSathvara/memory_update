package com.narmada.measure.screens.bareliserdi.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.bareliserdi.model.GetFormNumberRequest
import com.narmada.measure.screens.bareliserdi.model.PaniBandhDetailRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody

class BareliSerdiRepository constructor(private val retrofitService: RetrofitService) {
    suspend fun paniBandhDetail(requestModel: PaniBandhDetailRequest) = retrofitService.paniBandhDetail(requestModel)
    suspend fun getFormNumber(requestModel: GetFormNumberRequest) = retrofitService.getFormNumber(requestModel)
    suspend fun addBurnedCaneData(
        params: HashMap<String, RequestBody>,
        farmPhoto: MultipartBody.Part) = retrofitService.addBurnedCaneData(params, farmPhoto)
    suspend fun getCommonData() = retrofitService.getCommonData()

}