package com.narmada.measure.screens.bareliserdi.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.bareliserdi.model.AddBareliSerdiRequest
import com.narmada.measure.screens.bareliserdi.model.AddBareliSerdiResponse
import com.narmada.measure.screens.bareliserdi.model.CommonDataResponse
import com.narmada.measure.screens.bareliserdi.model.GetFormNumberRequest
import com.narmada.measure.screens.bareliserdi.model.GetFormNumberResponse
import com.narmada.measure.screens.bareliserdi.model.PaniBandhDetailRequest
import com.narmada.measure.screens.bareliserdi.model.PaniBandhDetailResponse
import com.narmada.measure.screens.bareliserdi.view.BareliSerdiActivity
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.screens.login.view.LoginActivity
import com.narmada.measure.utils.Const
import com.narmada.measure.utils.SharedPreferenceUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import retrofit2.HttpException
import java.io.File


class BareliSerdiViewModel(private val repository: BareliSerdiRepository) : ViewModel() {

    val paniBandObservable = MutableLiveData<PaniBandhDetailResponse>()
    val formNumberObservable = MutableLiveData<GetFormNumberResponse>()
    val commonDataObservable = MutableLiveData<CommonDataResponse>()
    val addBareliSerdiObservable = MutableLiveData<AddBareliSerdiResponse>()
    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()

    fun getPaniBandhDetailsApi(activity: BareliSerdiActivity, requestModel: PaniBandhDetailRequest) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.paniBandhDetail(requestModel)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        paniBandObservable.postValue(response.body())
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

    fun getFormNumberApi(activity: BareliSerdiActivity, requestModel: GetFormNumberRequest) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getFormNumber(requestModel)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        formNumberObservable.postValue(response.body())
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

    fun addBareliSerdiApi(activity: BareliSerdiActivity, requestModel: AddBareliSerdiRequest, khetar: File) {

        val bodyHashMap = HashMap<String, RequestBody>()
        bodyHashMap["account_id"] = Const.getRBofText(requestModel.account_id!!)
        bodyHashMap["computer_code"] = Const.getRBofText(requestModel.computer_code!!)
        bodyHashMap["moje_village_id"] = Const.getRBofText(requestModel.moje_village_id!!)
        bodyHashMap["ropan_or_laam_date"] = Const.getRBofText(requestModel.ropan_or_laam_date!!)
        bodyHashMap["sherdi_badeli_date"] = Const.getRBofText(requestModel.sherdi_badeli_date!!)
        bodyHashMap["mapni_type"] = Const.getRBofText(requestModel.mapni_type!!)
        bodyHashMap["item_id"] = Const.getRBofText(requestModel.item_id!!)
        bodyHashMap["kapat_id"] = Const.getRBofText(requestModel.kapat_id!!)
        bodyHashMap["total_area"] = Const.getRBofText(requestModel.total_area!!)
        bodyHashMap["burned_area"] = Const.getRBofText(requestModel.burned_area!!)
        bodyHashMap["working_year"] = Const.getRBofText(requestModel.working_year!!)
        bodyHashMap["burn_form_no"] = Const.getRBofText(requestModel.burn_form_no!!)
        bodyHashMap["date"] = Const.getRBofText(requestModel.date!!)
        bodyHashMap["time"] = Const.getRBofText(requestModel.time!!)

        val khetarPreview = khetar.asRequestBody("image/*".toMediaTypeOrNull())
        val khetarPart = MultipartBody.Part.createFormData(
            "farm_photo",
            "khetar" + System.currentTimeMillis() + ".jpg",
            khetarPreview
        )

        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.addBurnedCaneData(bodyHashMap, khetarPart)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        addBareliSerdiObservable.postValue(response.body())
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

    fun getCommonDataApi(activity: BareliSerdiActivity) {
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


}