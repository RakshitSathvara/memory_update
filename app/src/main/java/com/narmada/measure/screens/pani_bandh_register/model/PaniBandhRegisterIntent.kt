package com.narmada.measure.screens.pani_bandh_register.model

import com.narmada.measure.screens.bareliserdi.model.MapniTypes
import com.narmada.measure.screens.khetarmapni.model.VillageItem
import java.io.Serializable

data class PaniBandhRegisterIntent(

    val workingYear: String,
    val fromDate: String,
    val toDate: String,
    val supervisorId: String,
    val supervisorName: String,
    val zone: SupervisorZoneItem,
    val village: VillageItem,
    val mapniType: MapniTypes,
    val khetarCode: String,
    val sabhasadCode: String,

    ) : Serializable
