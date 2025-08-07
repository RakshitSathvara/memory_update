package com.narmada.measure.screens.kapni_supervisor.chalan_history.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.narmada.measure.R
import com.narmada.measure.network.Constants
import com.narmada.measure.screens.kapni_supervisor.chalan_history.model.DeliveryChalanHistoryResponse
import com.narmada.measure.screens.login.model.ErrorCommonResponse
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException


class DeliveryChalanHistoryViewModel(private val repository: DeliveryChalanHistoryRepository) : ViewModel() {

    var isLoading = false
    var isLastPage = false
    var currentPage = 1

    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()
    val paginationProgressObservable = MutableLiveData<Boolean>()
    val deliveryChalanListResponse = MutableLiveData<DeliveryChalanHistoryResponse>()

    fun getDeliveryChalanListApi(workingYear: String, fromDate: String, toDate: String, khetarCode: String) {

        val requestMap = hashMapOf<String, String>()
        requestMap["working_year"] = workingYear
        requestMap["start_date"] = fromDate
        requestMap["end_date"] = toDate
        requestMap["computer_code"] = khetarCode
        requestMap["page"] = currentPage.toString()

        isLoading = true
        paginationProgressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getDeliveryChalanList(requestMap)
                val result: DeliveryChalanHistoryResponse? = response.body()
                if (response.isSuccessful && result != null) {
                    if (result.response.equals(Constants.SUCCESS)) {
                        deliveryChalanListResponse.postValue(result!!)
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
                        deliveryChalanListResponse.postValue(DeliveryChalanHistoryResponse())
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