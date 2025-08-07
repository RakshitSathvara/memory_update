package com.narmada.measure.screens.ropan_register_report.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class RopanRegisterReportViewModelFactory constructor(private val repository: RopanRegisterReportRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RopanRegisterReportViewModel::class.java)) {
            RopanRegisterReportViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}