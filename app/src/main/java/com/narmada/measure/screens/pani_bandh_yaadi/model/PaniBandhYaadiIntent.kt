package com.narmada.measure.screens.pani_bandh_yaadi.model

import com.narmada.measure.screens.khetarmapni.model.VillageItem
import com.narmada.measure.screens.pani_bandh_register.model.SupervisorZoneItem
import java.io.Serializable

data class PaniBandhYaadiIntent(

    val workingYear: String,
    val supervisorId: String,
    val supervisorName: String,
    val zone: SupervisorZoneItem,
    val paniBandhDate: String,
    val village: VillageItem?,
    val computerCode: String?,

    ) : Serializable
