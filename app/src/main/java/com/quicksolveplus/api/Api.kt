package com.quicksolveplus.api

import com.google.gson.JsonObject
import com.quicksolveplus.authentication.models.CovidComplianceFormDetails
import com.quicksolveplus.authentication.models.ForgotPassword
import com.quicksolveplus.companies.models.Companies
import com.quicksolveplus.authentication.models.User
import com.quicksolveplus.covid.covid_result.test_result.models.Options
import com.quicksolveplus.covid.covid_result.test_result.models.TestingResult
import com.quicksolveplus.covid.covid_tracking.models.Covid
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import com.quicksolveplus.covid.covid_tracking.testing_site.models.CovidSite
import com.quicksolveplus.dashboard.models.Announcements
import com.quicksolveplus.dashboard.models.Worker
import com.quicksolveplus.qspmobile.clients.contacts.model.Contacts
import com.quicksolveplus.qspmobile.clients.dr_visits.model.ListDoctor
import com.quicksolveplus.qspmobile.clients.dr_visits.model.VisitDoctor
import com.quicksolveplus.qspmobile.clients.medication.model.Medication
import com.quicksolveplus.qspmobile.clients.models.Clients
import com.quicksolveplus.qspmobile.clients.models.EmergencyContact
import com.quicksolveplus.qspmobile.clients.models.Relation
import com.quicksolveplus.qspmobile.dashboard.pos.models.ClientPOS
import com.quicksolveplus.qspmobile.employee.model.Employees
import com.quicksolveplus.qspmobile.schedule.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * 16/03/23.
 *
 * @author hardkgosai.
 */
interface Api {

    companion object {
        //constant default server for devs
        const val defaultSever = "https://DEV.quicksolveplus.com:443"

        //public folder
        const val service = "QSPServices"

        //login constant
        const val validateUserLogon = "$service/ValidateUserLogon"
        const val forgotPassword = "$service/ForgotPassword"

        //dashboard
        const val getWorker = "$service/GetWorker"
        const val getEmployeeAnnouncements = "$service/GetEmployeeAnnouncements"
        const val getCovidComplianceFormDetails = "$service/GetCovidComplianceFormDetails"
        const val getClientsTrainedByEmployee = "$service/GetClientsTrainedByEmployee"
        const val getDashboard = "$service/GetDashboard"

        //companies
        const val getQSMobileWebClients = "$service/GetQSMobileWebClients"

        //clients
        const val getClients = "${service}/GetClients"
        const val getClientProfilePic = "${service}/GetClientProfilePic"
        const val getClientCaseloadOnly = "${service}/GetClientCaseloadOnly"
        const val getClientsForEmployeeOffices = "${service}/GetClientsForEmployeeOffices"
        const val getClientPOS = "${service}/GetClientPOS"

        //clients-preeti
        const val updateClient = "${service}/updateClient"
        const val getEmergencyContact = "${service}/GetEmergencyContact"
        const val updateEmergencyContact = "${service}/UpdateEmergencyContact"
        const val getOptionListWithUID = "${service}/GetOptionListWithUID"
        const val addNewEmergencyContact = "${service}/AddNewEmergencyContact"
        const val updateClientContact = "${service}/UpdateClientContact"
        const val addNewClientContact = "${service}/AddNewClientContact"
        const val getDoctorVisits = "${service}/GetDoctorVisits"
        const val getDoctors = "${service}/GetDoctors"
        const val updateDoctor = "${service}/UpdateDoctor"
        const val addNewDoctor = "${service}/AddNewDoctor"
        const val getClientMedicines = "${service}/GetClientMedicines"

        //employee
        const val getWorkers = "${service}/GetWorkers"
        const val getWorkerProfilePic = "${service}/GetWorkerProfilePic"
        const val getWorkersByMyCaseload = "${service}/GetWorkersByMyCaseload"
        const val getWorkersWithSameSupervisor = "${service}/GetWorkersWithSameSupervisor"
        const val getWorkersForEmployeeOffices = "${service}/GetWorkersForEmployeeOffices"

        //contacts
        const val getClientContacts = "${service}/GetClientContacts"

        //schedule
        const val getStaffPreferenceForScheduleByMonth =
            "${service}/GetStaffPreferenceForScheduleByMonth"
        const val getStaffTORForScheduleByMonth = "${service}/GetStaffTORForScheduleByMonth"
        const val getSchedulesByMonth = "${service}/GetSchedulesByMonth"
        const val getSchedules = "${service}/GetSchedules"
        const val getStaffPreferenceForSchedule = "${service}/GetStaffPreferenceForSchedule"
        const val getStaffTORForSchedule = "${service}/GetStaffTORForSchedule"
        const val getClientTrainedEmployees = "${service}/GetClientTrainedEmployees"
        const val getCustomClientServiceTypes = "${service}/GetCustomClientServiceTypes"
        const val getCustomEmployeeServiceTypes = "${service}/GetCustomEmployeeServiceTypes"
        const val getAttendanceTracking = "${service}/GetAttendanceTracking"
        const val getClientRemainingPOS = "${service}/GetClientRemainingPOS"
        const val getClientsForShiftPopup = "${service}/GetClientsForShiftPopup"
        const val getWorkersForShiftPopup = "${service}/GetWorkersForShiftPopup"
        const val getShiftSubCodes = "${service}/GetShiftSubCodes"
        const val getShiftObjectives = "${service}/GetShiftObjectives"
        const val getISPObjectivesForSchedule = "${service}/GetISPObjectivesForSchedule"

        // covid - bhavin
        const val getAllEmployeeCovidComplianceForms =
            "${service}/GetAllEmployeeCovidComplianceForms"
        const val getEmployeeCovidComplianceForms = "${service}/GetEmployeeCovidComplianceForms"
        const val getEmployeeTestingResults = "${service}/GetEmployeeTestingResults"
        const val submitCovidComplianceForm = "${service}/SubmitCovidComplianceForm"
        const val getImageFile = "${service}/GetImageFile"
        const val getSignatureFile = "${service}/GetSignatureFile"
        const val submitCovidTestingResult = "${service}/SubmitCovidTestingResult"
        const val getStaffPreferenceForScheduleByWeek =
            "${service}/GetStaffPreferenceForScheduleByWeek"
        const val getStaffTORForScheduleByWeek = "${service}/GetStaffTORForScheduleByWeek"
        const val getSchedulesByWeek = "${service}/GetSchedulesByWeek"

        // Covid Site(Map) - bhavin
        const val getTestingSites = "${service}/GetTestingSites"

        // Options - bhavin
        const val getOptionList = "${service}/GetOptionList"

        //Image upload and delete - bhavin
        const val uploadImageFile = "$service/UploadImageFile"
        const val deleteImageFile = "$service/DeleteImageFile"
    }

    @Headers("Content-Type:application/json")
    @POST(validateUserLogon)
    suspend fun validateUserLogon(@Body body: JsonObject): Response<User>

    @Headers("Content-Type:application/json")
    @POST(forgotPassword)
    suspend fun forgotPassword(@Body body: JsonObject): Response<ForgotPassword>

    @Headers("Content-Type:application/json")
    @POST(getQSMobileWebClients)
    suspend fun getQSMobileWebClients(): Response<Companies>

    @Headers("Content-Type:application/json")
    @POST(getWorker)
    suspend fun getWorker(@Body body: JsonObject): Response<Worker>

    @Headers("Content-Type:application/json")
    @POST(getEmployeeAnnouncements)
    suspend fun getEmployeeAnnouncements(@Body body: JsonObject): Response<Announcements>

    @Headers("Content-Type:application/json")
    @POST(getCovidComplianceFormDetails)
    suspend fun getCovidComplianceFormDetails(@Body body: JsonObject): Response<CovidComplianceFormDetails>

    @Headers("Content-Type:application/json")
    @POST(getClients)
    suspend fun getClients(@Body body: JsonObject): Response<Clients>

    @Headers("Content-Type:application/json")
    @POST(getClientCaseloadOnly)
    suspend fun getClientCaseloadOnly(@Body body: JsonObject): Response<Clients>

    @Headers("Content-Type:application/json")
    @POST(getClientsForEmployeeOffices)
    suspend fun getClientsForEmployeeOffices(@Body body: JsonObject): Response<Clients>

    @Headers("Content-Type:application/json")
    @POST(getClientsTrainedByEmployee)
    suspend fun getClientsTrainedByEmployee(@Body body: JsonObject): Response<Clients>

    @Headers("Content-Type:application/json")
    @POST(getClientProfilePic)
    suspend fun getClientProfilePic(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(getClientContacts)
    suspend fun getClientContacts(@Body body: JsonObject): Response<Contacts>

    @Headers("Content-Type:application/json")
    @POST(getWorkers)
    suspend fun getWorkers(@Body body: JsonObject): Response<Employees>

    @Headers("Content-Type:application/json")
    @POST(getWorkerProfilePic)
    suspend fun getWorkerProfilePic(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(getWorkersByMyCaseload)
    suspend fun getWorkersByMyCaseload(@Body body: JsonObject): Response<Employees>

    @Headers("Content-Type:application/json")
    @POST(getWorkersWithSameSupervisor)
    suspend fun getWorkersWithSameSupervisor(@Body body: JsonObject): Response<Employees>

    @Headers("Content-Type:application/json")
    @POST(getWorkersForEmployeeOffices)
    suspend fun getWorkersForEmployeeOffices(@Body body: JsonObject): Response<Employees>

    @Headers("Content-Type:application/json")
    @POST(getStaffPreferenceForScheduleByMonth)
    suspend fun getStaffPreferenceForScheduleByMonth(@Body body: JsonObject): Response<PreferenceScheduleByMonth>

    @Headers("Content-Type:application/json")
    @POST(getStaffTORForScheduleByMonth)
    suspend fun getStaffTORForScheduleByMonth(@Body body: JsonObject): Response<TORScheduleByMonth>

    @Headers("Content-Type:application/json")
    @POST(getSchedulesByMonth)
    suspend fun getSchedulesByMonth(@Body body: JsonObject): Response<ScheduleByMonth>

    @Headers("Content-Type:application/json")
    @POST(getSchedules)
    suspend fun getSchedules(@Body body: JsonObject): Response<Schedules>

    @Headers("Content-Type:application/json")
    @POST(getStaffPreferenceForSchedule)
    suspend fun getStaffPreferenceForSchedule(@Body body: JsonObject): Response<PreferenceSchedule>

    @Headers("Content-Type:application/json")
    @POST(getStaffTORForSchedule)
    suspend fun getStaffTORForSchedule(@Body body: JsonObject): Response<TORSchedule>

    @Headers("Content-Type:application/json")
    @POST(getClientPOS)
    suspend fun getClientPOS(@Body body: JsonObject): Response<ClientPOS>

    @Headers("Content-Type:application/json")
    @POST(getClientTrainedEmployees)
    suspend fun getClientTrainedEmployees(@Body body: JsonObject): Response<Employees>

    @Headers("Content-Type:application/json")
    @POST(getDashboard)
    suspend fun getDashboard(@Body body: JsonObject): Response<Dashboard>

    @Headers("Content-Type:application/json")
    @POST(getStaffPreferenceForScheduleByWeek)
    suspend fun getStaffPreferenceForScheduleByWeek(@Body body: JsonObject): Response<PreferenceSchedule>

    @Headers("Content-Type:application/json")
    @POST(getStaffTORForScheduleByWeek)
    suspend fun getStaffTORForScheduleByWeek(@Body body: JsonObject): Response<TORSchedule>

    @Headers("Content-Type:application/json")
    @POST(getSchedulesByWeek)
    suspend fun getSchedulesByWeek(@Body body: JsonObject): Response<Schedules>

    @Headers("Content-Type:application/json")
    @POST(getCustomClientServiceTypes)
    suspend fun getCustomClientServiceTypes(@Body body: JsonObject): Response<ServiceType>

    @Headers("Content-Type:application/json")
    @POST(getCustomEmployeeServiceTypes)
    suspend fun getCustomEmployeeServiceTypes(@Body body: JsonObject): Response<ServiceType>

    @Headers("Content-Type:application/json")
    @POST(getAttendanceTracking)
    suspend fun getAttendanceTracking(@Body body: JsonObject): Response<AttendanceTracking>

    @Headers("Content-Type:application/json")
    @POST(getClientRemainingPOS)
    suspend fun getClientRemainingPOS(@Body body: JsonObject): Response<String>

    @Headers("Content-Type:application/json")
    @POST(getClientsForShiftPopup)
    suspend fun getClientsForShiftPopup(@Body body: JsonObject): Response<Clients>

    @Headers("Content-Type:application/json")
    @POST(getWorkersForShiftPopup)
    suspend fun getWorkersForShiftPopup(@Body body: JsonObject): Response<Employees>

    @Headers("Content-Type:application/json")
    @POST(getShiftSubCodes)
    suspend fun getShiftSubCodes(@Body body: JsonObject): Response<ShiftSubCodes>

    @Headers("Content-Type:application/json")
    @POST(getShiftObjectives)
    suspend fun getShiftObjectives(@Body body: JsonObject): Response<ShiftObjectives>

    @Headers("Content-Type:application/json")
    @POST(getISPObjectivesForSchedule)
    suspend fun getISPObjectivesForSchedule(@Body body: JsonObject): Response<ISPObjectives>

    /*----------------------------------- Bhavin ---------------------------------------------*/

    @Headers("Content-Type:application/json")
    @POST(getAllEmployeeCovidComplianceForms)
    suspend fun getAllEmployeeCovidComplianceForms(@Body body: JsonObject): Response<Covid>

    @Headers("Content-Type:application/json")
    @POST(getEmployeeTestingResults)
    suspend fun getEmployeeTestingResults(@Body body: JsonObject): Response<TestingResult>

    @Headers("Content-Type:application/json")
    @POST(getOptionList)
    suspend fun getOptionList(@Body body: JsonObject): Response<Options>

    @Headers("Content-Type:application/json")
    @POST(getTestingSites)
    suspend fun getTestingSites(@Body body: JsonObject): Response<CovidSite>

    @Headers("Content-Type:application/json")
    @POST(uploadImageFile)
    suspend fun uploadImageFile(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(deleteImageFile)
    suspend fun deleteImageFile(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(submitCovidComplianceForm)
    suspend fun submitCovidComplianceForm(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(getImageFile)
    suspend fun getImageFile(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(getSignatureFile)
    suspend fun getSignatureFile(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(getEmployeeCovidComplianceForms)
    suspend fun getEmployeeCovidComplianceForms(@Body body: JsonObject): Response<CovidItem>

    @Headers("Content-Type:application/json")
    @POST(submitCovidTestingResult)
    suspend fun submitCovidTestingResult(@Body body: JsonObject): Response<CovidItem>

    /*---------------------------------------- Preeti -------------------------------------------*/

    @Headers("Content-Type:application/json")
    @POST(updateClient)
    suspend fun updateClient(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(getEmergencyContact)
    suspend fun getEmergencyContact(@Body body: JsonObject): Response<EmergencyContact>

    @Headers("Content-Type:application/json")
    @POST(updateEmergencyContact)
    suspend fun updateEmergencyContact(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(getOptionListWithUID)
    suspend fun getOptionListWithUID(@Body body: JsonObject): Response<Relation>

    @Headers("Content-Type:application/json")
    @POST(addNewEmergencyContact)
    suspend fun addNewEmergencyContact(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(updateClientContact)
    suspend fun updateClientContact(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(addNewClientContact)
    suspend fun addNewClientContact(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(getDoctorVisits)
    suspend fun getDoctorVisits(@Body body: JsonObject): Response<VisitDoctor>

    @Headers("Content-Type:application/json")
    @POST(getDoctors)
    suspend fun getDoctors(@Body body: JsonObject): Response<ListDoctor>

    @Headers("Content-Type:application/json")
    @POST(updateDoctor)
    suspend fun updateDoctor(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(addNewDoctor)
    suspend fun addNewDoctor(@Body body: JsonObject): Response<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST(getClientMedicines)
    suspend fun getClientMedicines(@Body body: JsonObject): Response<Medication>
}