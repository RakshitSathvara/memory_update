package com.narmada.measure.screens.kapni_complete_report.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class KapniCompleteReportViewModelFactory constructor(private val repository: KapniCompleteReportRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(KapniCompleteReportViewModel::class.java)) {
            KapniCompleteReportViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}