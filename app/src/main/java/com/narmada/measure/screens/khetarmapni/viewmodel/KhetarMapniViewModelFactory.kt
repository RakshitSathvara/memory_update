package com.narmada.measure.screens.khetarmapni.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class KhetarMapniViewModelFactory constructor(private val repository: KhetarMapniRepository, private val selectedYear: String?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(KhetarMapniViewModel::class.java)) {
            KhetarMapniViewModel(this.repository,selectedYear!!) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}