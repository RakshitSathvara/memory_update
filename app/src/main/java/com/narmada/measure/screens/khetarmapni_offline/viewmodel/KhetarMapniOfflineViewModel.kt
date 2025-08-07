package com.narmada.measure.screens.khetarmapni_offline.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narmada.measure.room.entity.OfflineMapni
import com.narmada.measure.screens.khetarmapni_offline.view.MapKhetarMapniOfflineActivity
import com.narmada.measure.utils.MapBoxUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class KhetarMapniOfflineViewModel(
    private val repository: KhetarMapniOfflineRepository
) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val errorIntMessage = MutableLiveData<Int>()
    val progressObservable = MutableLiveData<Boolean>()
    val insertOfflineMapni = MutableLiveData<Boolean>()
    val deleteOfflineMapni = MutableLiveData<Boolean>()
    val mapniList = MutableLiveData<List<OfflineMapni>>()

    fun insertOfflineMapni(offlineMapni: OfflineMapni) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.insert(offlineMapni)
                print(response)
                insertOfflineMapni.postValue(true)
                progressObservable.postValue(false)
            } catch (e: Exception) {
                print(e.printStackTrace())
                progressObservable.postValue(false)
            }
        }
    }

    fun getAllMapni() {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.getAllMapni()
                mapniList.postValue(response)
                progressObservable.postValue(false)
            } catch (e: Exception) {
                print(e.printStackTrace())
                progressObservable.postValue(false)
            }
        }
    }

    fun deleteOfflineMapni(offlineMapni: OfflineMapni) {
        progressObservable.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.delete(offlineMapni)
                print(response)
                getAllMapni()
                deleteOfflineMapni.postValue(true)
                progressObservable.postValue(false)
            } catch (e: Exception) {
                print(e.printStackTrace())
                progressObservable.postValue(false)
            }
        }
    }

    fun loadMBTilesToCacheDir(context: Context) {
        progressObservable.postValue(true)
//        Log.d("TAG", "loadMBTilesToCacheDir: COPY STARTED!!")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                MapBoxUtil.getFileFromAssets(
                    context,
                    MapKhetarMapniOfflineActivity.MBTILES_FILE_NAME
                )
//                Log.d("TAG", "loadMBTilesToCacheDir: COPY DONE!!")
                progressObservable.postValue(false)
            }
        }
    }

}