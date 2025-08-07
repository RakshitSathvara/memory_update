package com.narmada.measure.screens.dashboard.viewmodel

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.dashboard.model.CommonResponse
import com.narmada.measure.screens.dashboard.model.YearListResponse
import com.narmada.measure.screens.dashboard.view.DashboardActivity
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.screens.login.view.LoginActivity
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.SharedPreferenceUtil
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException


class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {

    val yearListResponse = MutableLiveData<YearListResponse>()
    val logoutResponse = MutableLiveData<CommonResponse>()
    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()

    fun yearList(activity: DashboardActivity, appVersion: String) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.yearList(appVersion)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        yearListResponse.postValue(response.body())
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }
                } else {
                    if (response.code() == 500) {
                        errorIntMessage.postValue(R.string.internal_server_error)
                    } else if (response.code() == 401) {
                        Const.showToast(activity, activity.getString(R.string.unauthorized_login_again))
                        SharedPreferenceUtil.putValue(Constants.IS_LOGIN, false)
                        SharedPreferenceUtil.save()
                        activity.startActivity(
                            Intent(
                                activity,
                                LoginActivity::class.java
                            )
                        )
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

    fun logout(activity: Activity) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.logout()
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        logoutResponse.postValue(response.body())
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }
                } else {
                    if (response.code() == 500) {
                        errorIntMessage.postValue(R.string.internal_server_error)
                    } else if (response.code() == 401) {
                        Const.showToast(activity, activity.getString(R.string.unauthorized_login_again))
                        SharedPreferenceUtil.putValue(Constants.IS_LOGIN, false)
                        SharedPreferenceUtil.save()
                        activity.startActivity(
                            Intent(
                                activity,
                                LoginActivity::class.java
                            )
                        )
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


}