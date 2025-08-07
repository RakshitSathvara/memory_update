package com.narmada.measure.screens.login.viewmodel

import com.narmada.measure.network.RetrofitService
import com.narmada.measure.screens.login.model.LoginRequest

class LoginRepository constructor(private val retrofitService: RetrofitService) {
    suspend fun login(loginRequest: LoginRequest) = retrofitService.login(loginRequest)

    suspend fun getSupervisorFace(supervisorId: String) = retrofitService.getSupervisorFace(supervisorId)

}