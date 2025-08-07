package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.kapni_supervisor.delivery_chalan.model.VahanNumberListResponse
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException


class VahanNumberListViewModel(private val repository: DeliveryChalanRepository) : ViewModel() {

    var isLoading = false
    var isLastPage = false
    var currentPage = 1

    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()
    val paginationProgressObservable = MutableLiveData<Boolean>()
    val vahanNumberListResponse = MutableLiveData<VahanNumberListResponse>()

    fun getVehicleNumberListApi(workingYear: String, search: String) {

        val perPageCount = 30

        val requestMap = hashMapOf<String, String>()
        requestMap["working_year"] = workingYear
        requestMap["search_keyword"] = search
        requestMap["page"] = currentPage.toString()
        requestMap["page_count"] = perPageCount.toString()

        isLoading = true
        paginationProgressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getVahanNumberList(requestMap)
                val result: VahanNumberListResponse? = response.body()
                if (response.isSuccessful && result != null) {
                    if (result.response.equals(Constants.SUCCESS)) {
                        vahanNumberListResponse.postValue(result!!)
                        if(result.data.size < perPageCount){
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
                        vahanNumberListResponse.postValue(VahanNumberListResponse())
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

}