package com.narmada.measure.screens.kapni_complete_report_view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class KapniPuriThayaniMahitiViewModelFactory constructor(private val repository: KapniPuriThayaniMahitiRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(KapniPuriThayaniMahitiViewModel::class.java)) {
            KapniPuriThayaniMahitiViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}