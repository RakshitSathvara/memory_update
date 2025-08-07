package com.narmada.measure.screens.kapni_complete_report.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.kapni_complete_report.model.KapniCompleteReportRequest
import com.narmada.measure.screens.kapni_complete_report.model.KapniCompleteReportResponse
import com.narmada.measure.screens.kapni_complete_report.model.KhetarCodeResponse
import com.narmada.measure.screens.kapni_complete_report.model.WeightListResponse
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerListResponse
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneListResponse
import com.narmada.measure.utils.Const
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import retrofit2.HttpException
import java.io.File


class KapniCompleteReportViewModel(private val repository: KapniCompleteReportRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val errorDialogMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()

    val zoneOfficerListResponse = MutableLiveData<ZoneOfficerListResponse>()
    val zoneListResponse = MutableLiveData<SupervisorZoneListResponse>()
    val khetarCodeListResponse = MutableLiveData<KhetarCodeResponse>()
    val weightListResponse = MutableLiveData<WeightListResponse>()
    val kapniCompleteReportResponse = MutableLiveData<KapniCompleteReportResponse>()

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

    fun getWeightList(computerCode: String, workingYear: String) {

        val requestMap = hashMapOf<String, String>()
        requestMap["Computer_Code"] = computerCode
        requestMap["Working_Year"] = workingYear

        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getWeightList(requestMap)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        weightListResponse.postValue(response.body())
                    } else {
                        errorDialogMessage.postValue(response.body()!!.message.toString())
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

    fun addKapniCompleteReportApi(reportRequest: KapniCompleteReportRequest, faceImage: File) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val bodyHashMap = HashMap<String, RequestBody>()
                bodyHashMap["working_year"] = Const.getRBofText(reportRequest.working_year)
                bodyHashMap["supervisor_id"] = Const.getRBofText(reportRequest.supervisor_id)
                bodyHashMap["zone_id"] = Const.getRBofText(reportRequest.zone_id)
                bodyHashMap["account_id"] = Const.getRBofText(reportRequest.account_id)
                bodyHashMap["computer_code"] = Const.getRBofText(reportRequest.computer_code)
                bodyHashMap["report_no"] = Const.getRBofText(reportRequest.report_no_view_only)
                bodyHashMap["report_no_custom"] = Const.getRBofText(reportRequest.report_no)

                val faceRequestBody = faceImage.asRequestBody("image/*".toMediaTypeOrNull())
                val facePart = MultipartBody.Part.createFormData("supervisor_image", faceImage.name, faceRequestBody)

                val response = repository.addKapniCompleteReport(bodyHashMap, facePart)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        kapniCompleteReportResponse.postValue(response.body())
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