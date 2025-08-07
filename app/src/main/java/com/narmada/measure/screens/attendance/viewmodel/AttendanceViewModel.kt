package com.narmada.measure.screens.attendance.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.attendance.model.AttendanceHistoryResponse
import com.narmada.measure.screens.dashboard.model.CommonResponse
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.khetarmapni.model.VillageListResponse
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.utils.Const
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import retrofit2.HttpException
import java.io.File


class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    val attendanceHistoryResponse = MutableLiveData<AttendanceHistoryResponse>()
    val addAttendanceResponse = MutableLiveData<CommonResponse>()
    val villageListResponse = MutableLiveData<VillageListResponse>()
    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()

    fun attendanceHistory(fromDate: String, toDate: String) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.attendanceHistory(fromDate, toDate)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        attendanceHistoryResponse.postValue(response.body())
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

    fun addAttendance(
        villageId: String,
        type: String,
        date: String,
        time: String,
        currentLocation: String,
        map: File?
    ) {
        val bodyHashMap = HashMap<String, RequestBody>()
        bodyHashMap["village_id"] = Const.getRBofText(villageId)
        bodyHashMap["type"] = Const.getRBofText(type)
        bodyHashMap["date"] = Const.getRBofText(date)
        bodyHashMap["time"] = Const.getRBofText(time)
        bodyHashMap["current_location"] = Const.getRBofText(currentLocation)

        val mapPreview: RequestBody
        var mapPart: MultipartBody.Part? = null
        if (map != null) {
            mapPreview = map.asRequestBody("image/*".toMediaTypeOrNull())
            mapPart = MultipartBody.Part.createFormData(
                "captured_selfie",
                "selfie" + System.currentTimeMillis() + ".png",
                mapPreview
            )
        }

        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.addAttendance(bodyHashMap, mapPart!!)
                print(response.headers())
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        addAttendanceResponse.postValue(response.body())
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

    fun villageList(jsonObject: VillageListRequest) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.villageList(jsonObject)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        villageListResponse.postValue(response.body())
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