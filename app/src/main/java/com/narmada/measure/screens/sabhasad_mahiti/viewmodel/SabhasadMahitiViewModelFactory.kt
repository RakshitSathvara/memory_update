package com.narmada.measure.screens.sabhasad_mahiti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class SabhasadMahitiViewModelFactory constructor(private val repository: SabhasadMahitiRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SabhasadMahitiViewModel::class.java)) {
            SabhasadMahitiViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}