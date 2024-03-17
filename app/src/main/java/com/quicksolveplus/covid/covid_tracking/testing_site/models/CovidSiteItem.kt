package com.quicksolveplus.covid.covid_tracking.testing_site.models

data class CovidSiteItem(
    val AgencyUrl: String,
    val County: String,
    val FullAddress: String,
    val HasDriveThrough: String,
    val HasSameDayResults: String,
    val IsAppointmentOnly: String,
    val ObjectID: Int,
    val OperationHours: String,
    val PhoneNumber: String,
    val SiteLatitude: String,
    val SiteLongitude: String,
    val SiteName: String,
    val State: String,
    val TestType: String
)