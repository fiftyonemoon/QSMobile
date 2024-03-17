package com.quicksolveplus.qspmobile.clients.contacts.model

import com.google.gson.annotations.SerializedName

data class ContactsItem(
    @SerializedName("Address1")
    var Address1: String = "",
    @SerializedName("Address2")
    var Address2: String = "",
    @SerializedName("BackupEmployee")
    val BackupEmployee: Boolean = false,
    @SerializedName("CellIsPrimary")
    val CellIsPrimary: Int = -1,
    @SerializedName("CellPhone")
    var CellPhone: String = "",
    @SerializedName("City")
    var City: String = "",
    @SerializedName("clientUID")
    var ClientUID: Int = -1,
    @SerializedName("Comments")
    val Comments: String = "",
    @SerializedName("CompanyAddress1")
    var CompanyAddress1: String = "",
    @SerializedName("CompanyAddress2")
    var CompanyAddress2: String = "",
    @SerializedName("CompanyCity")
    var CompanyCity: String = "",
    @SerializedName("CompanyEmail")
    val CompanyEmail: String = "",
    @SerializedName("CompanyFaxNumber")
    var CompanyFaxNumber: String = "",
    @SerializedName("CompanyName")
    var CompanyName: String = "",
    @SerializedName("CompanyPhone")
    var CompanyPhone: String = "",
    @SerializedName("CompanyState")
    var CompanyState: String = "",
    @SerializedName("CompanyZip")
    var CompanyZip: String = "",
    @SerializedName("ContactType")
    var ContactType: Int = -1,
    @SerializedName("ContactTypeDesc")
    var ContactTypeDesc: String = "",
    @SerializedName("Email")
    var Email: String = "",
    @SerializedName("FaxNumber")
    var FaxNumber: String = "",
    @SerializedName("FirstName")
    var FirstName: String = "",
    @SerializedName("HomeIsPrimary")
    val HomeIsPrimary: Int = -1,
    @SerializedName("HomePhone")
    var HomePhone: String = "",
    @SerializedName("IsConservator")
    val IsConservator: Int = -1,
    @SerializedName("IsEmergencyContact")
    val IsEmergencyContact: Int = -1,
    @SerializedName("IsEvacuationContact")
    val IsEvacuationContact: Int = -1,
    @SerializedName("IsRepPayee")
    val IsRepPayee: Int = -1,
    @SerializedName("LastName")
    var LastName: String = "",
    @SerializedName("MiddleName")
    var MiddleName: String = "",
    @SerializedName("Name")
    val Name: String = "",
    @SerializedName("PrimaryEmployee")
    val PrimaryEmployee: Boolean = false,
    @SerializedName("Relationship")
    var Relationship: String = "",
    @SerializedName("ShowOnEap")
    val ShowOnEap: Int = -1,
    @SerializedName("ShowOnFaceSheet")
    val ShowOnFaceSheet: Int = -1,
    @SerializedName("State")
    var State: String = "",
    @SerializedName("Status")
    val Status: String = "",
    @SerializedName("UID")
    var UID: Int = -1,
    @SerializedName("WorkIsPrimary")
    val WorkIsPrimary: Int = -1,
    @SerializedName("WorkPhone")
    var WorkPhone: String = "",
    @SerializedName("WorkPhoneExt")
    val WorkPhoneExt: String = "",
    @SerializedName("Zip")
    var Zip: String = ""
)