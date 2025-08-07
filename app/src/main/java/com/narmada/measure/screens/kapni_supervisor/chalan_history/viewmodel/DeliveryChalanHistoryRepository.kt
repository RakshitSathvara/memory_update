package com.narmada.measure.screens.kapni_supervisor.chalan_history.viewmodel

import com.narmada.measure.network.RetrofitService

class DeliveryChalanHistoryRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getDeliveryChalanList(jsonObject: HashMap<String, String>) = retrofitService.getDeliveryChalanList(jsonObject)

}