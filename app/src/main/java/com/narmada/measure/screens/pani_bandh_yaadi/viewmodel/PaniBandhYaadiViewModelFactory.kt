package com.narmada.measure.screens.pani_bandh_yaadi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class PaniBandhYaadiViewModelFactory constructor(private val repository: PaniBandhYaadiRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PaniBandhYaadiViewModel::class.java)) {
            PaniBandhYaadiViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(PaniBandhYaadiListViewModel::class.java)) {
            PaniBandhYaadiListViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}