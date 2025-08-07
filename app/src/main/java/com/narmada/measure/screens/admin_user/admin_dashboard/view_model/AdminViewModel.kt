package com.narmada.measure.screens.admin_user.admin_dashboard.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.admin_user.admin_dashboard.model.AddFaceDataResponse
import com.narmada.measure.screens.dashboard.model.CommonResponse
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import retrofit2.HttpException
import java.io.File


class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    val addFaceResponse = MutableLiveData<AddFaceDataResponse>()
    val deleteFaceResponse = MutableLiveData<CommonResponse>()
    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()

    fun addSupervisor(
        supervisorId: RequestBody,
        faceData: RequestBody,
        supervisorImage: File?
    ) {
        progressObservable.postValue(true)

        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer " + Constants.ADMIN_TOKEN

        val mapPreview: RequestBody
        var mapPart: MultipartBody.Part? = null
        if (supervisorImage != null) {
            mapPreview = supervisorImage.asRequestBody("image/*".toMediaTypeOrNull())
            mapPart = MultipartBody.Part.createFormData(
                "face_image",
                "face" + System.currentTimeMillis() + ".png",
                mapPreview
            )
        }

        //with co-routine
        viewModelScope.launch {
            try {
                val response = repository.addSupervisorFace(supervisorId, faceData, mapPart!!, headers)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        addFaceResponse.postValue(response.body())
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }
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

    fun deleteSupervisor(supervisorId: String) {
        progressObservable.postValue(true)

        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer " + Constants.ADMIN_TOKEN

        //with co-routine
        viewModelScope.launch {
            try {
                val response = repository.deleteSupervisorFace(supervisorId,headers)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        deleteFaceResponse.postValue(response.body())
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }
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