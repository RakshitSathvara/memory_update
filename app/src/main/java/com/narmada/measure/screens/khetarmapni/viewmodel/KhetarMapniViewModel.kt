package com.narmada.measure.screens.khetarmapni.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.room.entity.OfflineMapni
import com.narmada.measure.screens.khetarmapni.model.AccountMemberRequest
import com.narmada.measure.screens.khetarmapni.model.AccountMemberResponse
import com.narmada.measure.screens.khetarmapni.model.CheckComputerCodeRequest
import com.narmada.measure.screens.khetarmapni.model.CheckComputerCodeResponse
import com.narmada.measure.screens.khetarmapni.model.CommonApiResponse
import com.narmada.measure.screens.khetarmapni.model.KhetarMapniRequest
import com.narmada.measure.screens.khetarmapni.model.SearchSabhaSadResponse
import com.narmada.measure.screens.khetarmapni.model.SubmitKhetarMapniResponse
import com.narmada.measure.screens.khetarmapni.model.VillageListRequest
import com.narmada.measure.screens.khetarmapni.model.VillageListResponse
import com.narmada.measure.screens.khetarmapni.model.ZoneOfficerListResponse
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import com.narmada.measure.utils.Const
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File


class KhetarMapniViewModel(private val repository: KhetarMapniRepository,private val selectedYear: String) : ViewModel() {

    val searchSabhaSadResponse = MutableLiveData<SearchSabhaSadResponse>()
    val zoneOfficerListResponse = MutableLiveData<ZoneOfficerListResponse>()
    val commonApiResponse = MutableLiveData<CommonApiResponse>()
    val checkComputerCodeResponse = MutableLiveData<CheckComputerCodeResponse>()
    val addKhetarMapniResponse = MutableLiveData<SubmitKhetarMapniResponse>()
    val villageListResponse = MutableLiveData<VillageListResponse>()
    val accountMemberResponse = MutableLiveData<AccountMemberResponse>()
    val errorMessage = MutableLiveData<String>()
    val shareCountZeroMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()
    val deleteOfflineMapni = MutableLiveData<Boolean>()


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

    fun commonApi(toString: String) {
        viewModelScope.launch {
            try {
                val response = repository.commonApi(selectedYear)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        commonApiResponse.postValue(response.body())
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
                errorMessage.postValue(e.message)
                e.printStackTrace()
            } catch (e: Throwable) {
                errorMessage.postValue(e.message)
                e.printStackTrace()
            }
        }
    }

    fun searchSabhaSad(text: String) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.searchSabhaSad(text)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        searchSabhaSadResponse.postValue(response.body())
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

    fun checkComputerCode(jsonObject: CheckComputerCodeRequest) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.checkComputerCode(jsonObject)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        checkComputerCodeResponse.postValue(response.body())
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

    fun accountMemberDetail(jsonObject: AccountMemberRequest) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.accountMemberDetail(jsonObject)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        accountMemberResponse.postValue(response.body())
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }
                } else {
                    if (response.code() == 500) {
                        errorIntMessage.postValue(R.string.internal_server_error)
                    } else if (response.code() == 401) {
                        errorIntMessage.postValue(R.string.unauthorized_login_again)
                    }  else if (response.code() == 422) {
                        try {
                            val errorCommonResponse: ErrorCommonResponse =
                                Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorCommonResponse::class.java
                                )
                            shareCountZeroMessage.postValue(errorCommonResponse.message.toString())
                        } catch (err: JSONException) {
                            Log.d("Error", err.toString())
                        }
                    }else {
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

    fun addKhetarMapni(jsonObject: KhetarMapniRequest, map: File?, khetar: File?) {
        val bodyHashMap = HashMap<String, RequestBody>()
        bodyHashMap["zone_officer_id"] = Const.getRBofText(jsonObject.zoneOfficerId.toString())
        bodyHashMap["account_id"] = Const.getRBofText(jsonObject.accountId.toString())
        bodyHashMap["computer_code"] = Const.getRBofText(jsonObject.computerCode.toString())
        bodyHashMap["moje_village_id"] = Const.getRBofText(jsonObject.mojeVillageId.toString())
        bodyHashMap["nondh_village_id"] = Const.getRBofText(jsonObject.nondhVillageId.toString())
        bodyHashMap["approx_ropan_date"] = Const.getRBofText(jsonObject.approxRopanDate.toString())
        bodyHashMap["mapni_type"] = Const.getRBofText(jsonObject.mapniType.toString())
        bodyHashMap["item_id"] = Const.getRBofText(jsonObject.itemId.toString())
        bodyHashMap["piyat_sadhan"] = Const.getRBofText(jsonObject.piyatSadhan.toString())
        bodyHashMap["serial_number"] = Const.getRBofText(jsonObject.serialNumber.toString())
        bodyHashMap["biyaran_id"] = Const.getRBofText(jsonObject.biyaranId.toString())
        bodyHashMap["ropan_area"] = Const.getRBofText(jsonObject.ropanArea.toString())
        bodyHashMap["khetar_name"] = Const.getRBofText(jsonObject.khetarName.toString())
        bodyHashMap["working_year"] = Const.getRBofText(jsonObject.workingYear.toString())
        bodyHashMap["pilan_season"] = Const.getRBofText(jsonObject.pilanSeason.toString())
        bodyHashMap["polygon_json"] = Const.getRBofText(jsonObject.polygonJson.toString())
        bodyHashMap["not_org"] = Const.getRBofText(jsonObject.notOrganic.toString())

        bodyHashMap["farm_owner_name_for_north_direction"] = Const.getRBofText(jsonObject.northKhetarName.toString())
        bodyHashMap["farm_owner_name_for_south_direction"] = Const.getRBofText(jsonObject.southKhetarName.toString())
        bodyHashMap["farm_owner_name_for_west_direction"] = Const.getRBofText(jsonObject.westKhetarName.toString())
        bodyHashMap["farm_owner_name_for_east_direction"] = Const.getRBofText(jsonObject.eastKhetarName.toString())
        bodyHashMap["trench_count"] = Const.getRBofText(jsonObject.chasNumber.toString())
        bodyHashMap["trench_direction"] = Const.getRBofText(jsonObject.chasDirection.toString())

        val mapPreview: RequestBody
        val khetarPreview: RequestBody
        var mapPart: MultipartBody.Part? = null
        var khetarPart: MultipartBody.Part? = null
        if (map != null) {
            mapPreview = map.asRequestBody("image/*".toMediaTypeOrNull())
            mapPart = MultipartBody.Part.createFormData(
                "polygon_photo",
                "map" + System.currentTimeMillis() + ".jpg",
                mapPreview
            )
        }

        if (khetar != null) {
            khetarPreview = khetar.asRequestBody("image/*".toMediaTypeOrNull())
            khetarPart = MultipartBody.Part.createFormData(
                "farm_photo",
                "khetar" + System.currentTimeMillis() + ".jpg",
                khetarPreview
            )
        }

        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.addKhetarMapni(bodyHashMap, mapPart!!, khetarPart!!)
                progressObservable.postValue(false)
                if (response.isSuccessful) {
                    if (response.body()!!.response.equals(Constants.SUCCESS)) {
                        addKhetarMapniResponse.postValue(response.body())
                    } else {
                        errorMessage.postValue(response.body()!!.message.toString())
                    }
                } else {
                    if (response.code() == 500) {
                        var isErrorPosted = false
                        val responseBody = response.errorBody()
                        if(responseBody != null) {
                            val errorBody = responseBody.string()
                            val json = JSONObject(errorBody)
                            if (json.has("validation_error") && json.getJSONObject("validation_error").length() > 0) {
                                val errorJson = json.getJSONObject("validation_error")
                                errorJson.keys().forEach { key ->
                                    if(!isErrorPosted) {
                                        errorMessage.postValue(errorJson.getJSONArray(key).getString(0))
                                        isErrorPosted = true
                                    }
                                }
                            }
                        }

                        if(!isErrorPosted) {
                            errorIntMessage.postValue(R.string.internal_server_error)
                        }
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

    fun deleteOfflineMapni(offlineMapni: OfflineMapni) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.delete(offlineMapni)
                print(response)
                deleteOfflineMapni.postValue(true)
                progressObservable.postValue(false)
            } catch (e: Exception) {
                print(e.printStackTrace())
                progressObservable.postValue(false)
            }
        }
    }
}