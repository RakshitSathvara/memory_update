package com.narmada.measure.screens.sabhasad_mahiti_update.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class SabhasadMahitiUpdateViewModelFactory constructor(private val repository: SabhasadMahitiUpdateRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SabhasadMahitiUpdateViewModel::class.java)) {
            SabhasadMahitiUpdateViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}