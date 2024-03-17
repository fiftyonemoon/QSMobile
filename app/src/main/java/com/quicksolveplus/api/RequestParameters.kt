package com.quicksolveplus.api

import com.google.gson.JsonObject
import com.quicksolveplus.qspmobile.schedule.models.ServiceType
import com.quicksolveplus.utils.Preferences

/**
 * 16/03/23.
 * To make a request object [JsonObject].
 *
 * @author hardkgosai.
 */
object RequestParameters {

    //for login
    private const val userId = "userId"
    private const val password = "password"
    private const val deviceType = "deviceType"
    private const val deviceToken = "deviceToken"

    //for forgot password
    private const val email = "Email"
    private const val appType = "AppType"

    private const val userSecurityLevel = "userSecurityLevel"
    private const val userLevel1 = "userLevel1"
    private const val userLevel2 = "userLevel2"
    private const val userUID = "userUID"
    private const val staffID = "staffID"
    private const val staffUID = "staffUID"
    private const val employeeId = "employeeId"
    private const val isForSchedule = "isForSchedule"
    private const val clientID = "clientID"
    private const val photo = "Photo"
    private const val level1 = "Level1"
    private const val dDay = "dDay"
    private const val dMonth = "dMonth"
    private const val dYear = "dYear"
    private const val isForDsn = "isForDsn"
    private const val schedDate = "SchedDate"
    private const val POSType = "POSType"
    private const val scheduleDate = "scheduleDate"
    private const val dMonthStart = "dMonthStart"
    private const val dDayStart = "dDayStart"
    private const val dYearStart = "dYearStart"
    private const val dMonthEnd = "dMonthEnd"
    private const val dDayEnd = "dDayEnd"
    private const val dYearEnd = "dYearEnd"
    private const val shiftMethod = "ShiftMethod"
    private const val scheduleId = "scheduleId"
    private const val scheduleUID = "scheduleUID"
    private const val serviceType = "ServiceType"
    private const val isDoctorRecord = "IsDoctorRecord"

    //bhavin's merge
    private const val optionListId = "optionListId"
    private const val UserId = "UserId"
    private const val base64Image = "base64Image"
    private const val zip = "zip"
    private const val county = "county"
    private const val fileName = "fileName"
    private const val imageUploadCode = "imageUploadCode"
    private const val imageTypeCode = "imageTypeCode"

    private const val doctor = "Doctor"
    private const val clientUID = "clientUID"
    private const val ClientUID = "ClientUID"
    private const val visitDate = "VisitDate"
    private const val visitStartTime = "VisitStartTime"
    private const val visitEndTime = "VisitEndTime"
    private const val reason = "Reason"
    private const val outcome = "Outcome"
    private const val newMedication = "NewMedication"
    private const val nextVisitDate = "NextVisitDate"
    private const val nextVisitStartTime = "NextVisitStartTime"
    private const val nextVisitEndTime = "NextVisitEndTime"
    private const val uID = "UID"

    fun forLogin(
        userId: String? = "",
        password: String? = "",
        deviceType: String? = "android",
        deviceToken: String? = Preferences.instance?.deviceToken ?: ""
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.userId, userId)
            addProperty(RequestParameters.password, password)
            addProperty(RequestParameters.deviceType, deviceType)
            addProperty(RequestParameters.deviceToken, deviceToken)
        }
    }

    fun forForgotPassword(
        email: String? = "",
        appType: String? = "QSMobile",
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.email, email)
            addProperty(RequestParameters.appType, appType)
        }
    }

    fun forWorker(staffID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffID, staffID)
        }
    }

    fun forEmployeeAnnouncements(employeeId: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.employeeId, employeeId)
        }
    }

    fun forCovidComplianceFormDetails(employeeId: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.employeeId, employeeId)
        }
    }

    fun forClientData(
        userSecurityLevel: Int? = -1,
        userLevel1: Int? = -1,
        userLevel2: Int? = -1,
        userUID: Int? = -1
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.userSecurityLevel, userSecurityLevel)
            addProperty(RequestParameters.userLevel1, userLevel1)
            addProperty(RequestParameters.userLevel2, userLevel2)
            addProperty(RequestParameters.userUID, userUID)
        }
    }

    fun forEmployeeData(
        userSecurityLevel: Int? = -1,
        userLevel1: Int? = -1,
        userLevel2: Int? = -1,
        staffID: Int? = -1
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.userSecurityLevel, userSecurityLevel)
            addProperty(RequestParameters.userLevel1, userLevel1)
            addProperty(RequestParameters.userLevel2, userLevel2)
            addProperty(RequestParameters.staffID, staffID)
        }
    }

    fun forProfilePicture(
        photo: String? = "", level1: Int? = -1
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.photo, photo)
            addProperty(RequestParameters.level1, level1)
        }
    }

    fun forClientCaseloadOnly(
        userUID: Int? = -1
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.userUID, userUID)
        }
    }

    fun forWorkersByMyCaseload(
        isForSchedule: Boolean = false, staffID: Int? = -1
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.isForSchedule, isForSchedule)
            addProperty(RequestParameters.staffID, staffID)
        }
    }

    fun forClientContact(clientID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
        }
    }

    fun forClientsForEmployeeOffices(userUID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.userUID, userUID)
        }
    }

    fun forClientsTrainedByEmployee(userUID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.userUID, userUID)
        }
    }

    fun forWorkersForEmployeeOffices(staffID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffID, staffID)
        }
    }

    fun forWorkersWithSameSupervisor(staffID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffID, staffID)
        }
    }

    fun forStaffPreferenceForScheduleByMonth(
        staffUID: Int? = -1, dMonth: String? = "", dYear: String? = ""
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffUID, staffUID)
            addProperty(RequestParameters.dMonth, dMonth)
            addProperty(RequestParameters.dYear, dYear)
        }
    }

    fun forStaffTORForScheduleByMonth(
        staffUID: Int? = -1, dMonth: String? = "", dYear: String? = ""
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffUID, staffUID)
            addProperty(RequestParameters.dMonth, dMonth)
            addProperty(RequestParameters.dYear, dYear)
        }
    }

    fun forSchedulesByMonth(
        staffUID: Int? = -1,
        ClientUID: Int? = -1,
        dMonth: String? = "",
        dYear: String? = "",
        isForDsn: Boolean? = false
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffUID, staffUID)
            addProperty(RequestParameters.ClientUID, ClientUID)
            addProperty(RequestParameters.dMonth, dMonth)
            addProperty(RequestParameters.dYear, dYear)
            addProperty(RequestParameters.isForDsn, isForDsn)
        }
    }

    fun forStaffPreferenceForSchedule(
        staffID: Int? = -1, schedDate: String? = ""
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffID, staffID)
            addProperty(RequestParameters.schedDate, schedDate)
        }
    }

    fun forStaffTORForSchedule(
        staffID: Int? = -1, schedDate: String? = ""
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffID, staffID)
            addProperty(RequestParameters.schedDate, schedDate)
        }
    }

    fun forSchedules(
        staffUID: Int? = -1,
        ClientUID: Int? = -1,
        dDay: Int? = -1,
        dMonth: Int? = -1,
        dYear: Int? = -1,
        isForDsn: Boolean? = false
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffUID, staffUID)
            addProperty(RequestParameters.ClientUID, ClientUID)
            addProperty(RequestParameters.dDay, dDay)
            addProperty(RequestParameters.dMonth, dMonth)
            addProperty(RequestParameters.dYear, dYear)
            addProperty(RequestParameters.isForDsn, isForDsn)
        }
    }

    fun forClientPOS(
        clientUID: Int? = -1, dMonth: Int? = -1, dYear: Int? = -1, POSType: String? = ""
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientUID, clientUID)
            addProperty(RequestParameters.dMonth, dMonth)
            addProperty(RequestParameters.dYear, dYear)
            addProperty(RequestParameters.POSType, POSType)
        }
    }

    fun forClientTrainedEmployees(clientID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
        }
    }

    fun forDashboard(staffID: Int? = -1, scheduleDate: String): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffID, staffID)
            addProperty(RequestParameters.scheduleDate, scheduleDate)
        }
    }

    fun forStaffPreferenceForScheduleByWeek(
        staffUID: Int? = -1,
        dMonthStart: Int? = -1,
        dDayStart: Int? = -1,
        dYearStart: Int? = -1,
        dMonthEnd: Int? = -1,
        dDayEnd: Int? = -1,
        dYearEnd: Int? = -1,
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffUID, staffUID)
            addProperty(RequestParameters.dMonthStart, dMonthStart)
            addProperty(RequestParameters.dDayStart, dDayStart)
            addProperty(RequestParameters.dYearStart, dYearStart)
            addProperty(RequestParameters.dMonthEnd, dMonthEnd)
            addProperty(RequestParameters.dDayEnd, dDayEnd)
            addProperty(RequestParameters.dYearEnd, dYearEnd)
        }
    }

    fun forStaffTORForScheduleByWeek(
        staffUID: Int? = -1,
        dMonthStart: Int? = -1,
        dDayStart: Int? = -1,
        dYearStart: Int? = -1,
        dMonthEnd: Int? = -1,
        dDayEnd: Int? = -1,
        dYearEnd: Int? = -1,
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffUID, staffUID)
            addProperty(RequestParameters.dMonthStart, dMonthStart)
            addProperty(RequestParameters.dDayStart, dDayStart)
            addProperty(RequestParameters.dYearStart, dYearStart)
            addProperty(RequestParameters.dMonthEnd, dMonthEnd)
            addProperty(RequestParameters.dDayEnd, dDayEnd)
            addProperty(RequestParameters.dYearEnd, dYearEnd)
        }
    }

    fun forStaffSchedulesByWeek(
        staffUID: Int? = -1,
        clientUID: Int? = -1,
        dMonthStart: Int? = -1,
        dDayStart: Int? = -1,
        dYearStart: Int? = -1,
        dMonthEnd: Int? = -1,
        dDayEnd: Int? = -1,
        dYearEnd: Int? = -1,
        isForDsn: Boolean,
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffUID, staffUID)
            addProperty(RequestParameters.clientUID, clientUID)
            addProperty(RequestParameters.dMonthStart, dMonthStart)
            addProperty(RequestParameters.dDayStart, dDayStart)
            addProperty(RequestParameters.dYearStart, dYearStart)
            addProperty(RequestParameters.dMonthEnd, dMonthEnd)
            addProperty(RequestParameters.dDayEnd, dDayEnd)
            addProperty(RequestParameters.dYearEnd, dYearEnd)
            addProperty(RequestParameters.isForDsn, isForDsn)
        }
    }

    fun forCustomClientServiceTypes(clientID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
        }
    }

    fun forCustomEmployeeServiceTypes(employeeId: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.employeeId, employeeId)
        }
    }

    fun forAttendanceTracking(scheduleId: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.scheduleId, scheduleId)
        }
    }

    fun forClientRemainingPOS(
        clientID: Int? = -1, serviceType: String? = "", scheduleDate: String? = ""
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
            addProperty(RequestParameters.serviceType, serviceType)
            addProperty(RequestParameters.scheduleDate, scheduleDate)
        }
    }

    fun forClientsForShiftPopup(
        userUID: Int? = -1,
        userLevel1: Int? = -1,
        userLevel2: Int? = -1,
        userSecurityLevel: Int? = -1,
        serviceType: String? = ""
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.userUID, userUID)
            addProperty(RequestParameters.userLevel1, userLevel1)
            addProperty(RequestParameters.userLevel2, userLevel2)
            addProperty(RequestParameters.userSecurityLevel, userSecurityLevel)
            addProperty(RequestParameters.serviceType, serviceType)
        }
    }

    fun forWorkersForShiftPopup(
        staffID: Int? = -1,
        userLevel1: Int? = -1,
        userLevel2: Int? = -1,
        userSecurityLevel: Int? = -1,
        serviceType: String? = "",
        isDoctorRecord: Boolean? = false,
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffID, staffID)
            addProperty(RequestParameters.userLevel1, userLevel1)
            addProperty(RequestParameters.userLevel2, userLevel2)
            addProperty(RequestParameters.userSecurityLevel, userSecurityLevel)
            addProperty(RequestParameters.serviceType, serviceType)
            addProperty(RequestParameters.isDoctorRecord, isDoctorRecord)
        }
    }

    fun forShiftSubCodes(scheduleUID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.scheduleUID, scheduleUID)
        }
    }

    fun forShiftObjectives(scheduleUID: Int? = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.scheduleUID, scheduleUID)
        }
    }

    fun forISPObjectivesForSchedule(
        clientID: Int? = -1, serviceType: String? = "", schedDate: String? = ""
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
            addProperty(RequestParameters.serviceType, serviceType)
            addProperty(RequestParameters.schedDate, schedDate)
        }
    }

    //---------------------------------------- Bhavin ------------------------------------------

    fun forGetEmployeeAnnouncements(employeeId: Int): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.employeeId, employeeId)
        }
    }

    fun forGetAllEmployeeCovidComplianceForms(employeeId: Int): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.employeeId, employeeId)
        }
    }

    fun forGetTestingSites(county: String, zip: String): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.county, county)
            addProperty(RequestParameters.zip, zip)
        }
    }

    fun forGetEmployeeTestingResults(employeeId: Int): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.employeeId, employeeId)
        }
    }

    fun forGetOptionsList(optionListId: Int): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.optionListId, optionListId)
        }
    }

    fun forUploadImageFile(UserId: Int, base64Image: String, imageUploadCode: Int): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.UserId, UserId)
            addProperty(RequestParameters.base64Image, base64Image)
            addProperty(RequestParameters.imageUploadCode, imageUploadCode)
        }
    }

    fun forDeleteImageFile(fileName: String, imageUploadCode: Int): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.fileName, fileName)
            addProperty(RequestParameters.imageUploadCode, imageUploadCode)
        }
    }

    fun forSubmitCovidComplianceForm(): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.fileName, fileName)
            addProperty(RequestParameters.imageUploadCode, optionListId)
        }
    }

    fun forGetImageFile(fileName: String = "", imageTypeCode: Int): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.fileName, fileName)
            addProperty(RequestParameters.imageTypeCode, imageTypeCode)
        }
    }

    fun forGetSignatureFile(fileName: String = ""): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.fileName, fileName)
        }
    }

    fun forGetEmployeeCovidComplianceForms(employeeId: Int): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.employeeId, employeeId)
        }
    }


    /*----------------------------------------- Preeti ---------------------------------------*/

    fun forWorkersWithSameSupervisor(staffID: Int = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffID, staffID)
        }
    }

    fun forWorkersForEmployeeOffices(staffID: Int = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.staffID, staffID)
        }
    }

    fun forClientContact(clientID: Int): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
        }
    }

    fun forEmergencyContact(clientID: Int = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
        }
    }

    fun forOptionListWithUID(optionListId: Int = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.optionListId, optionListId)
        }
    }

    fun forOptionListWithUID(optionListId: Int = -1, shiftMethod: String): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.optionListId, optionListId)
            addProperty(RequestParameters.shiftMethod, shiftMethod)
        }
    }

    fun forDoctorVisits(clientID: Int = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
        }
    }

    fun forDoctorList(clientID: Int = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
        }
    }

    fun forSaveDoctor(
        doctor: String,
        clientUID: Int,
        visitDate: String,
        visitStartTime: String,
        visitEndTime: String,
        reason: String,
        outcome: String,
        newMedication: String,
        nextVisitDate: String,
        nextVisitStartTime: String,
        nextVisitEndTime: String,
        uID: Int?
    ): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.doctor, doctor)
            addProperty(RequestParameters.ClientUID, clientUID)
            addProperty(RequestParameters.visitDate, visitDate)
            addProperty(RequestParameters.visitStartTime, visitStartTime)
            addProperty(RequestParameters.visitEndTime, visitEndTime)
            addProperty(RequestParameters.reason, reason)
            addProperty(RequestParameters.outcome, outcome)
            addProperty(RequestParameters.newMedication, newMedication)
            addProperty(RequestParameters.nextVisitDate, nextVisitDate)
            addProperty(RequestParameters.nextVisitStartTime, nextVisitStartTime)
            addProperty(RequestParameters.nextVisitEndTime, nextVisitEndTime)
            addProperty(RequestParameters.uID, uID)
        }
    }

    fun forMedicationList(clientID: Int = -1): JsonObject {
        return JsonObject().apply {
            addProperty(RequestParameters.clientID, clientID)
        }
    }
}