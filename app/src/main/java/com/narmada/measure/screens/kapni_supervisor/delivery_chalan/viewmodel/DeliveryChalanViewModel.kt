package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.bareliserdi.model.CommonDataResponse
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.AddDeliveryChalanRequest
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.AddDeliveryChalanResponse
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.CaneTypeListResponse
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.KapniPaniBandhDetailResponse
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.screens.login.view.LoginActivity
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.SharedPreferenceUtil
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException


class DeliveryChalanViewModel(private val repository: DeliveryChalanRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()

    val commonDataObservable = MutableLiveData<CommonDataResponse>()
    val caneTypeListResponse = MutableLiveData<CaneTypeListResponse>()
    val paniBandhDetailResponse = MutableLiveData<KapniPaniBandhDetailResponse>()
    val addDeliveryChalanResponse = MutableLiveData<AddDeliveryChalanResponse>()

    fun getCommonDataApi(activity: Activity) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getCommonData()
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        commonDataObservable.postValue(response.body())
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }
                } else {
                    if (response.code() == 500) {
                        errorIntMessage.postValue(R.string.internal_server_error)
                    } else if (response.code() == 401) {
                        Const.showToast(activity,activity.getString(R.string.unauthorized_login_again))
                        SharedPreferenceUtil.putValue(Constants.IS_LOGIN,false)
                        SharedPreferenceUtil.save()
                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                        activity.finish()

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

    fun zoneOfficerList() {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getCaneTypeList()
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        caneTypeListResponse.postValue(response.body())
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

    fun kapniSupervisorPaniBandhDetail(workingYear: String, khetarCode: String) {

        val requestMap = hashMapOf<String, String>()
        requestMap["computer_code"] = khetarCode
        requestMap["working_year"] = workingYear

        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.kapniSupervisorPaniBandhDetail(requestMap)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        paniBandhDetailResponse.postValue(response.body())
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

    fun addCaneDeliveryChalanApi(reportRequest: AddDeliveryChalanRequest) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.addCaneDeliveryChalan(reportRequest)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        addDeliveryChalanResponse.postValue(response.body())
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