package com.narmada.measure.screens.sabhasad_mahiti_update.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.sabhasad_mahiti_update.model.MemberDetailByCodeRequest
import com.narmada.measure.screens.sabhasad_mahiti_update.model.SabhasadMahitiUpdateRequest

class SabhasadMahitiUpdateRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getMemberDetailByCode(jsonObject: MemberDetailByCodeRequest) = retrofitService.getMemberDetailByCode(jsonObject.account_id!!)
    suspend fun updateMemberDetailByCode(jsonObject: SabhasadMahitiUpdateRequest) = retrofitService.updateMemberDetailByCode(jsonObject)

}