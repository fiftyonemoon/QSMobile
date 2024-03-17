package com.quicksolveplus.dashboard.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Worker(
    @SerializedName("ActiveUserId")
    val activeUserId: Any,
    @SerializedName("Address")
    val address: String,
    @SerializedName("AutoInsExpDate")
    val autoInsExpDate: String,
    @SerializedName("AvailFormDone")
    val availFormDone: String,
    @SerializedName("AvailFormExpDate")
    val availFormExpDate: String,
    @SerializedName("BackSafety")
    val backSafety: Int,
    @SerializedName("BackSafetyExpDate")
    val backSafetyExpDate: String,
    @SerializedName("BloodPath")
    val bloodPath: Int,
    @SerializedName("BloodPathExpDate")
    val bloodPathExpDate: Any,
    @SerializedName("CNA")
    val cNA: Int,
    @SerializedName("CNAExpDate")
    val cNAExpDate: String,
    @SerializedName("CPRCertExpDate")
    val cPRCertExpDate: String,
    @SerializedName("CPRCertification")
    val cPRCertification: String,
    @SerializedName("CanDrive")
    val canDrive: String,
    @SerializedName("CareEffect")
    val careEffect: Int,
    @SerializedName("CareEffectExpDate")
    val careEffectExpDate: String,
    @SerializedName("CellPhone")
    val cellPhone: String,
    @SerializedName("City")
    val city: String,
    @SerializedName("CompCoinBalance")
    val compCoinBalance: Double,
    @SerializedName("CurrentPosition")
    val currentPosition: String,
    @SerializedName("DDS")
    val dDS: Int,
    @SerializedName("DDSExpDate")
    val dDSExpDate: Any,
    @SerializedName("DMVprintout")
    val dMVprintout: String,
    @SerializedName("Email")
    val email: String,
    @SerializedName("FaxNumber")
    val faxNumber: String,
    @SerializedName("FirstAidCert")
    val firstAidCert: String,
    @SerializedName("FirstAidExpDate")
    val firstAidExpDate: String,
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("HHA")
    val hHA: Int,
    @SerializedName("HHAExpDate")
    val hHAExpDate: Any,
    @SerializedName("HasAutoIns")
    val hasAutoIns: String,
    @SerializedName("HasBooster")
    val hasBooster: Boolean,
    @SerializedName("HasCar")
    val hasCar: String,
    @SerializedName("HasCluAccount")
    val hasCluAccount: Boolean,
    @SerializedName("HomePhone")
    val homePhone: String,
    @SerializedName("I9onFile")
    val i9onFile: String,
    @SerializedName("IHDPROP")
    val iHDPROP: Double,
    @SerializedName("IHSSWorker")
    val iHSSWorker: String,
    @SerializedName("ILSAdminROP")
    val iLSAdminROP: Double,
    @SerializedName("ILSROP")
    val iLSROP: Double,
    @SerializedName("ILSTrainROP")
    val iLSTrainROP: Double,
    @SerializedName("ILSTravelROP")
    val iLSTravelROP: Double,
    @SerializedName("LVN")
    val lVN: Int,
    @SerializedName("LVNExpDate")
    val lVNExpDate: Any,
    @SerializedName("LastName")
    val lastName: String,
    @SerializedName("LiveScan")
    val liveScan: String,
    @SerializedName("LiveScanDate")
    val liveScanDate: String,
    @SerializedName("MandateFormDone")
    val mandateFormDone: String,
    @SerializedName("MsgOp_Email")
    val msgOpEmail: Boolean,
    @SerializedName("MsgOp_PushNotification")
    val msgOpPushNotification: Boolean,
    @SerializedName("MsgOp_SMS")
    val msgOpSMS: Boolean,
    @SerializedName("MsgOp_Signature")
    val msgOpSignature: String,
    @SerializedName("Name")
    val name: String,
    @SerializedName("NickName")
    val nickName: String,
    @SerializedName("OSHA")
    val oSHA: Int,
    @SerializedName("OSHAExpDate")
    val oSHAExpDate: String,
    @SerializedName("OtherTS1")
    val otherTS1: Int,
    @SerializedName("OtherTS1ExpDate")
    val otherTS1ExpDate: Any,
    @SerializedName("OtherTS2")
    val otherTS2: Int,
    @SerializedName("OtherTS2ExpDate")
    val otherTS2ExpDate: Any,
    @SerializedName("OtherTS3")
    val otherTS3: Int,
    @SerializedName("OtherTS3ExpDate")
    val otherTS3ExpDate: Any,
    @SerializedName("OtherTS4")
    val otherTS4: Int,
    @SerializedName("OtherTS4ExpDate")
    val otherTS4ExpDate: Any,
    @SerializedName("OtherTS5")
    val otherTS5: Int,
    @SerializedName("OtherTS5ExpDate")
    val otherTS5ExpDate: Any,
    @SerializedName("OtherTSDesc1")
    val otherTSDesc1: Any,
    @SerializedName("OtherTSDesc2")
    val otherTSDesc2: Any,
    @SerializedName("OtherTSDesc3")
    val otherTSDesc3: Any,
    @SerializedName("OtherTSDesc4")
    val otherTSDesc4: Any,
    @SerializedName("OtherTSDesc5")
    val otherTSDesc5: Any,
    @SerializedName("PSTAllotted")
    val pSTAllotted: Double,
    @SerializedName("PSTRemaining")
    val pSTRemaining: Double,
    @SerializedName("PSTUsed")
    val pSTUsed: Double,
    @SerializedName("PSTrainPOS")
    val pSTrainPOS: Double,
    @SerializedName("ParentSuppROP")
    val parentSuppROP: Double,
    @SerializedName("ProvideServ")
    val provideServ: Int,
    @SerializedName("RN")
    val rN: Int,
    @SerializedName("RNExpDate")
    val rNExpDate: String,
    @SerializedName("RefComplete")
    val refComplete: String,
    @SerializedName("RespIll")
    val respIll: Int,
    @SerializedName("RespIllExpDate")
    val respIllExpDate: Any,
    @SerializedName("SLSPhil")
    val sLSPhil: Int,
    @SerializedName("SLSPhilExpDate")
    val sLSPhilExpDate: Any,
    @SerializedName("SLSPremROP")
    val sLSPremROP: Double,
    @SerializedName("SLSStandROP")
    val sLSStandROP: Double,
    @SerializedName("State")
    val state: String,
    @SerializedName("TBTest")
    val tBTest: String,
    @SerializedName("TBTestExpDate")
    val tBTestExpDate: String,
    @SerializedName("Title17")
    val title17: Int,
    @SerializedName("Title17ExpDate")
    val title17ExpDate: String,
    @SerializedName("UsersUID")
    val usersUID: Int,
    @SerializedName("VaccineStatus")
    val vaccineStatus: String,
    @SerializedName("WorkAuth")
    val workAuth: String,
    @SerializedName("WorkAuthExpDate")
    val workAuthExpDate: String,
    @SerializedName("WorkComp")
    val workComp: Int,
    @SerializedName("WorkCompExpDate")
    val workCompExpDate: Any,
    @SerializedName("WorkPhone")
    val workPhone: String,
    @SerializedName("WorkPhoneExtension")
    val workPhoneExtension: String,
    @SerializedName("WorkerProfilePic")
    val workerProfilePic: String,
    @SerializedName("Zip")
    val zip: String
) : Serializable