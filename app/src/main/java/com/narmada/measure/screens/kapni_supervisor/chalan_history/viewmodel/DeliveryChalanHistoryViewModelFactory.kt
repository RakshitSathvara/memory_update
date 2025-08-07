package com.narmada.measure.screens.kapni_supervisor.chalan_history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class DeliveryChalanHistoryViewModelFactory constructor(private val repository: DeliveryChalanHistoryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(DeliveryChalanHistoryViewModel::class.java)) {
            DeliveryChalanHistoryViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}