package com.narmada.measure.screens.kapni_complete_report_view.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.kapni_complete_report.model.KhetarCodeResponse
import com.narmada.measure.screens.kapni_complete_report_view.model.KapniPuriMahitiReportDownloadResponse
import com.narmada.measure.screens.kapni_complete_report_view.model.KapniPuriThayaniMahitiIntent
import com.narmada.measure.screens.kapni_complete_report_view.model.KapniPuriThayaniMahitiListResponse
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerListResponse
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneListResponse
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException


class KapniPuriThayaniMahitiViewModel(private val repository: KapniPuriThayaniMahitiRepository) :
    ViewModel() {

    var isLoading = true
    var isLastPage = false

    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()
    val paginationProgressObservable = MutableLiveData<Boolean>()

    val zoneOfficerListResponse = MutableLiveData<ZoneOfficerListResponse>()
    val zoneListResponse = MutableLiveData<SupervisorZoneListResponse>()
    val kapniCompleteReportListResponse = MutableLiveData<KapniPuriThayaniMahitiListResponse>()
    val khetarCodeListResponse = MutableLiveData<KhetarCodeResponse>()
    val kapniPuriMahitiDownloadResponse = MutableLiveData<KapniPuriMahitiReportDownloadResponse>()

    fun zoneOfficerList() {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.zoneOfficerList()
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        zoneOfficerListResponse.postValue(response.body())
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

    fun supervisorZoneList(supervisorId: String) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.supervisorZoneList(supervisorId)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        zoneListResponse.postValue(response.body())
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

    fun kapniCompleteReportList(reportRequest: KapniPuriThayaniMahitiIntent) {

        val requestMap = hashMapOf<String, String>()
        requestMap["working_year"] = reportRequest.workingYear
        requestMap["supervisor_id"] = reportRequest.supervisorId
        requestMap["zone_id"] = reportRequest.zone.zoneId ?: ""
        requestMap["computer_code"] = reportRequest.khetarCode
        requestMap["account_id"] = reportRequest.sabhasadCode

        isLoading = true
        paginationProgressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.kapniCompleteReportList(requestMap)
                val result: KapniPuriThayaniMahitiListResponse? = response.body()
                if (response.isSuccessful && result != null) {
                    if (result.response.equals(Constants.SUCCESS)) {
                        kapniCompleteReportListResponse.postValue(result!!)
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }

                    isLoading = false
                    isLastPage = true
                    paginationProgressObservable.postValue(false)
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

    fun getKhetarCodeList(sabhasadNumber: String, workingYear: String) {

        val requestMap = hashMapOf<String, String>()
        requestMap["Account_id"] = sabhasadNumber
        requestMap["Working_Year"] = workingYear

        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getKhetarCodeList(requestMap)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        khetarCodeListResponse.postValue(response.body())
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

    fun kapniCompleteReportDownload(reportRequest: KapniPuriThayaniMahitiIntent) {
        val requestMap = hashMapOf<String, String>()
        requestMap["Working_Year"] = reportRequest.workingYear
        requestMap["Supervisor_Id"] = reportRequest.supervisorId
        requestMap["Zone_Id"] = reportRequest.zone.zoneId ?: ""
        requestMap["Computer_Code"] = reportRequest.khetarCode
        requestMap["Account_Id"] = reportRequest.sabhasadCode

        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                // TODO: update request api...
                val response = repository.kapniCompleteReportDownload(requestMap)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        kapniPuriMahitiDownloadResponse.postValue(response.body())
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