package com.narmada.measure.screens.kapni_complete_report_view.model

import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneItem
import java.io.Serializable

data class KapniPuriThayaniMahitiIntent(

    val workingYear: String,
    val supervisorId: String,
    val supervisorName: String,
    val zone: SupervisorZoneItem,
    val khetarCode: String,
    val sabhasadCode: String,

    ) : Serializable
