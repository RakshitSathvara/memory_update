package com.narmada.measure.screens.badeli_serdi_report.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class BadeliSerdiReportViewModelFactory constructor(private val repository: BadeliSerdiReportRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BadeliSerdiReportViewModel::class.java)) {
            BadeliSerdiReportViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}