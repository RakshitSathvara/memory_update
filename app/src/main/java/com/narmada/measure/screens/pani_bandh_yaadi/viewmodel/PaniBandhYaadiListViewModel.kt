package com.narmada.measure.screens.pani_bandh_yaadi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.screens.pani_bandh_register.model.FarmMeasurementReportRequest
import com.narmada.measure.screens.pani_bandh_register.model.FarmMeasurementReportResponse
import com.narmada.measure.screens.pani_bandh_yaadi.model.PaniBandhYaadiListRequest
import com.narmada.measure.screens.pani_bandh_yaadi.model.PaniBandhYaadiListResponse
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException


class PaniBandhYaadiListViewModel(private val repository: PaniBandhYaadiRepository) : ViewModel() {

    var isLoading = true
    var isLastPage = false
    var currentPage = 1

    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()
    val paginationProgressObservable = MutableLiveData<Boolean>()
    val paniBandhYaadiListResponse = MutableLiveData<PaniBandhYaadiListResponse>()
    val farmMeasurementReportResponse = MutableLiveData<FarmMeasurementReportResponse>()

    fun getPaniBandhRegisterListApi(reportRequest: PaniBandhYaadiListRequest) {

        val requestMap = hashMapOf<String, String>()
        requestMap["working_year"] = reportRequest.working_year
//        requestMap["supervisor_id"] = reportRequest.supervisor_id
        requestMap["zone_id"] = reportRequest.zone_id
        requestMap["date"] = reportRequest.date
        requestMap["village_id"] = reportRequest.village_id ?: ""
        requestMap["computer_code"] = reportRequest.computer_code ?: ""
        requestMap["page"] = currentPage.toString()

        isLoading = true
        paginationProgressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getPaniBandhYaadiReportList(requestMap)
                val result: PaniBandhYaadiListResponse? = response.body()
                if (response.isSuccessful && result != null) {
                    if (result.response.equals(Constants.SUCCESS)) {
                        paniBandhYaadiListResponse.postValue(result!!)
                        if(currentPage >= (result.data?.pagination?.lastPage ?: 0)){
                            isLastPage = true
                            paginationProgressObservable.postValue(false)
                        }
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                        isLastPage = true
                        paginationProgressObservable.postValue(false)
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
//                            errorMessage.postValue(errorCommonResponse.message.toString())
                        } catch (err: JSONException) {
                            Log.d("Error", err.toString())
                        }
                        paniBandhYaadiListResponse.postValue(PaniBandhYaadiListResponse())
                    }
                    isLastPage = true
                    paginationProgressObservable.postValue(false)
                }
                isLoading = false
            } catch (e: HttpException) {
                paginationProgressObservable.postValue(false)
                errorMessage.postValue(e.message)
                e.printStackTrace()
                isLoading = false
                isLastPage = true
            } catch (e: Throwable) {
                paginationProgressObservable.postValue(false)
                errorMessage.postValue(e.message)
                e.printStackTrace()
                isLoading = false
                isLastPage = true
            }
        }
    }

    fun generateFarmMeasurementReportApi(reportRequest: FarmMeasurementReportRequest) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.generateFarmMeasurementReport(reportRequest)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        farmMeasurementReportResponse.postValue(response.body())
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