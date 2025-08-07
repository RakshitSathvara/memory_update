package com.narmada.measure.screens.khetar_mapni_report.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class KhetarMapniReportViewModelFactory constructor(private val repository: KhetarMapniReportRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(KhetarMapniReportViewModel::class.java)) {
            KhetarMapniReportViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}