package com.narmada.measure.screens.dashboard.model

import java.io.Serializable

data class YearIntentModel(
    val years: List<YearsItem?>? = null,
    val selectedYear: String? = null
):Serializable
