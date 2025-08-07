package com.narmada.measure.screens.sabhasad_mahiti_update.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.screens.sabhasad_mahiti_update.model.MemberDetailByCodeRequest
import com.narmada.measure.screens.sabhasad_mahiti_update.model.MemberDetailByCodeResponse
import com.narmada.measure.screens.sabhasad_mahiti_update.model.SabhasadMahitiUpdateRequest
import com.narmada.measure.screens.sabhasad_mahiti_update.model.SabhasadMahitiUpdateResponse
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException


class SabhasadMahitiUpdateViewModel(private val repository: SabhasadMahitiUpdateRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()

    val memberDetailByCodeResponse = MutableLiveData<MemberDetailByCodeResponse>()
    val updateMemberDetailByCodeResponse = MutableLiveData<SabhasadMahitiUpdateResponse>()

    fun getMemberDetailByCode(reportRequest: MemberDetailByCodeRequest) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getMemberDetailByCode(reportRequest)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        memberDetailByCodeResponse.postValue(response.body())
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }
                } else {
                    if (response.code() == 500) {
                        errorIntMessage.postValue(R.string.internal_server_error)
                    } else if (response.code() == 401) {
                        errorIntMessage.postValue(R.string.unauthorized_login_again)
                    } else {
                        try {
                            val errorCommonResponse: ErrorCommonResponse =
                                Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorCommonResponse::class.java
                                )
                            errorMessage.postValue(errorCommonResponse.message.toString())
                        } catch (err: JSONException) {
                            Log.d("Error", err.toString())
                        }
                    }
                }
            } catch (e: HttpException) {
                progressObservable.postValue(false)
                errorMessage.postValue(e.message)
                e.printStackTrace()
            } catch (e: Throwable) {
                progressObservable.postValue(false)
                errorMessage.postValue(e.message)
                e.printStackTrace()
            }
        }
    }

    fun updateMemberDetailByCode(reportRequest: SabhasadMahitiUpdateRequest) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.updateMemberDetailByCode(reportRequest)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        updateMemberDetailByCodeResponse.postValue(response.body())
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }
                } else {
                    if (response.code() == 500) {
                        errorIntMessage.postValue(R.string.internal_server_error)
                    } else if (response.code() == 401) {
                        errorIntMessage.postValue(R.string.unauthorized_login_again)
                    } else {
                        try {
                            val errorCommonResponse: ErrorCommonResponse =
                                Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorCommonResponse::class.java
                                )
                            errorMessage.postValue(errorCommonResponse.message.toString())
                        } catch (err: JSONException) {
                            Log.d("Error", err.toString())
                        }
                    }
                }
            } catch (e: HttpException) {
                progressObservable.postValue(false)
                errorMessage.postValue(e.message)
                e.printStackTrace()
            } catch (e: Throwable) {
                progressObservable.postValue(false)
                errorMessage.postValue(e.message)
                e.printStackTrace()
            }
        }
    }

}