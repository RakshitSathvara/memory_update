package com.narmada.measure.screens.khetarmapni_offline.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class KhetarMapniOfflineViewModelFactory constructor(
    private val repository: KhetarMapniOfflineRepository,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(KhetarMapniOfflineViewModel::class.java)) {
            KhetarMapniOfflineViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}