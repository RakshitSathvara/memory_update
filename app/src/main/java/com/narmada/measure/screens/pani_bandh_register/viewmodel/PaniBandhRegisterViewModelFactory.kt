package com.narmada.measure.screens.pani_bandh_register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class PaniBandhRegisterViewModelFactory constructor(private val repository: PaniBandhRegisterRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PaniBandhRegisterViewModel::class.java)) {
            PaniBandhRegisterViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(PaniBandhRegisterListViewModel::class.java)) {
            PaniBandhRegisterListViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}