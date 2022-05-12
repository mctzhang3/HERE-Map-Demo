package com.mzhang.heremapdemo.locationLanding.model

import com.here.sdk.core.GeoCoordinates


data class MemberInfo(
    val phoneNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val nickName: String = "",
    val geoCoordinates: GeoCoordinates,
    val nearLocation: String = "",
    val timeSince: String = "",
    val batteryLevel: String = "90%",
    val favorite: Boolean = false
)