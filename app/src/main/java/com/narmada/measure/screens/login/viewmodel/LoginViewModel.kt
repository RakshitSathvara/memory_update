package com.narmada.measure.screens.login.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.screens.login.model.LoginRequest
import com.narmada.measure.screens.login.model.LoginResponse
import com.narmada.measure.screens.login.model.SupervisorFaceResponse
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException


class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    val loginResponse = MutableLiveData<LoginResponse>()
    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()
    val supervisorFaceResponse = MutableLiveData<SupervisorFaceResponse>()

    fun login(loginRequest: LoginRequest) {
        progressObservable.postValue(true)

        // without co-routine
//         val response = repository.login(loginRequest)
//        response.enqueue(object : Callback<LoginResponse> {
//            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                progressObservable.postValue(false)
//                if (response.isSuccessful) {
//                    loginResponse.postValue(response.body())
//                } else {
//                    errorMessage.postValue("Any error message")
//                }
//            }
//
//            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                errorMessage.postValue(t.message)
//                progressObservable.postValue(false)
//                t.printStackTrace()
//            }
//        })

        //with co-routine
        viewModelScope.launch {
            try {
                val response = repository.login(loginRequest)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        loginResponse.postValue(response.body())
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

    fun getSupervisorFaceData(supervisorId: String) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getSupervisorFace(supervisorId)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        supervisorFaceResponse.postValue(response.body())
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