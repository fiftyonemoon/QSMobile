package com.quicksolveplus.qspmobile.clients.models

data class EmergencyContactItem(
    var Address: String = "",
    var CellPhone: String = "",
    var City: String = "",
    var ClientUID: Int = 0,
    var Email: String = "",
    var FaxNumber: String = "",
    var FirstName: String = "",
    var HomePhone: String = "",
    var LastName: String = "",
    val Name: String = "",
    var Relationship: String = "",
    var State: String = "",
    var UID: Int = 0,
    var WorkPhone: String = "",
    var Zip: String = ""
)