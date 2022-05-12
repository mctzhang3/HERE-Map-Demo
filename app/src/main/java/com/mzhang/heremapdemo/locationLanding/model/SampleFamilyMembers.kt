package com.mzhang.heremapdemo.locationLanding.model

import com.here.sdk.core.GeoCoordinates

object SampleFamilyMembers {
    private val phoneNumbers: MutableList<String> = arrayListOf("5551112222", "5551112221", "5552223333", "5553331111")
    private val nicknames: MutableList<String> = arrayListOf("Mark", "David", "Amy", "Mary")
    private val nearLocations: MutableList<String> =
        arrayListOf("Near 593 Main St.", "Near North Ave NE", "Near 10th St", "Near 2466 Lake Rd")
    private val timeSinceList: MutableList<String> =
        arrayListOf("Since 8:30 am", "Since 10:01 am", "Since 1:03 pm", "Since 11:23 am")
    val MAP_CENTER_GEO_COORDINATES = GeoCoordinates(33.748997, -84.387985)

    fun getSampleFamilyMemberInfo(): List<MemberInfo> {
        val memberList: MutableList<MemberInfo> = arrayListOf()

        for (i in 0 .. 3) {
            memberList.add(
                MemberInfo(
                geoCoordinates = createRandomGeoCoordinates(),
                phoneNumber = phoneNumbers[i],
                nickName = nicknames[i],
                nearLocation = nearLocations[i],
                timeSince = timeSinceList[i]
            )
            )
        }
        return memberList
    }

    private fun createRandomGeoCoordinates(): GeoCoordinates {
        val centerGeoCoordinates = GeoCoordinates(33.748997, -84.387985)
        val lat: Double = centerGeoCoordinates.latitude
        val lon: Double = centerGeoCoordinates.longitude
        return GeoCoordinates(
            getRandom(lat - 0.02, lat + 0.02),
            getRandom(lon - 0.02, lon + 0.02)
        )
    }

    private fun getRandom(min: Double, max: Double): Double {
        return min + Math.random() * (max - min)
    }
}
