package com.narmada.measure.screens.khetarmapni_offline.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Dipti Agravat on 02,August,2023
 */

data class OfflineMapniModel(
    var khetarCode: String? = null,
    var sabhasadCode: String? = null,
    var ropanArea: String? = null,
    var polygonJson: String? = null,
    var mapImage: String? = null,
    var khetarImage: String? = null,
    var northKhetarName: String? = null,
    var southKhetarName: String? = null,
    var westKhetarName: String? = null,
    var eastKhetarName: String? = null,
    var chasNumber: String? = null,
    var chasDirection: String? = null,
    var chasDirectionName: String? = null,
) : Serializable
