package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.AddDeliveryChalanRequest

class DeliveryChalanRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getCommonData() = retrofitService.getCommonData()
    suspend fun getCaneTypeList() = retrofitService.getCaneTypeList()
    suspend fun kapniSupervisorPaniBandhDetail(jsonObject: HashMap<String, String>) = retrofitService.kapniSupervisorPaniBandhDetail(jsonObject)
    suspend fun getVahanNumberList(jsonObject: HashMap<String, String>) = retrofitService.getVahanNumberList(jsonObject)
    suspend fun getMukadamNumberList(jsonObject: HashMap<String, String>) = retrofitService.getMukadamNumberList(jsonObject)
    suspend fun addCaneDeliveryChalan(jsonObject: AddDeliveryChalanRequest) = retrofitService.addCaneDeliveryChalan(jsonObject)

}