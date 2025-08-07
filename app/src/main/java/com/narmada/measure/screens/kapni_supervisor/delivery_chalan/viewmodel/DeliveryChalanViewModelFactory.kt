package com.narmada.measure.screens.kapni_supervisor.delivery_chalan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class DeliveryChalanViewModelFactory constructor(private val repository: DeliveryChalanRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(DeliveryChalanViewModel::class.java)) {
            DeliveryChalanViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(VahanNumberListViewModel::class.java)) {
            VahanNumberListViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(MukadamNumberListViewModel::class.java)) {
            MukadamNumberListViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}