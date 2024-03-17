package com.quicksolveplus.utils

import com.quicksolveplus.authentication.models.UserPermission

/**
 * 24/03/23.
 *
 * @author hardkgosai.
 */
object QSPermissions {

    private const val PermissionQSMessages = "QSMessages"
    private const val PermissionAnnouncements = "Announcements"
    private const val PermissionClientScheduleView = "Client Schedule View"
    private const val PermissionEmployeeScheduleView = "Employee Schedule View"
    private const val PermissionClientModule = "Client Module"
    private const val PermissionEmployeeModule = "Employee Module"
    private const val PermissionScheduleModule = "Schedule Module"
    private const val PermissionEmployeeOTDashboard = "Employee OT Dashboard"
    private const val PermissionScheduleModification = "Schedule Modification"
    private const val PermissionViewTrainedClientsOnly = "View Trained Clients Only"
    private const val PermissionViewOfficeClientsOnly = "View Office Clients Only"
    private const val PermissionViewClientCaseloadOfAssignedSupervisorOnly =
        "View Client Caseload of Assigned Supervisor Only"
    private const val PermissionEmployeeUserRelatedInfoOnly = "Employee User Related Info Only"
    private const val PermissionViewUserScheduleOnly = "View User Schedule Only"
    private const val PermissionViewOfficeEmployeesOnly = "View Office Employees Only"
    private const val PermissionViewSameSupervisedEmployeesAsUser =
        "View Same Supervised Employees as User"

    private const val PermissionMyTimesheetsMaintenance = "My Timesheets Maintenance"
    private const val PermissionClientContactsMaintenance = "Client Contacts Maintenance"
    private const val PermissionClientDoctorVisitMaintenance = "Client Doctor Visit Maintenance"
    private const val PermissionClientMedRxMaintenance = "Client MedRx Maintenance"
    private const val PermissionClientAllergiesMaintenance = "Client Allergies Maintenance"
    private const val PermissionClientPOSMaintenance = "Client POS Maintenance"
    private const val PermissionClientISPMaintenance = "Client ISP Maintenance"
    private const val PermissionClientEAPMaintenance = "Client EAP Maintenance"
    private const val PermissionClientFIRMaintenance = "Client FIR Maintenance"
    private const val PermissionClientConnectMaintenance = "Client Connect Maintenance"
    private const val PermissionClientQualityAssuranceMaintenance =
        "Client Quality Assurance Maintenance"
    private const val PermissionClientDocumentMaintenance = "Client Document Maintenance"
    private const val PermissionClientTrainingGuideMaintenance = "Client Training Guide Maintenance"
    private const val PermissionClientInfoMaintenance = "Client Info Maintenance"
    private const val PermissionClientSSNMaintenance = "Client SSN Maintenance"
    private const val PermissionViewSameSupervisedEmployeesUser =
        "View Same Supervised Employees as User"
    private const val PermissionPriorCurrentNextMonthViewOnly =
        "Prior, Current and Next Month View Only"
    private const val PermissionClientPOSIHSSDashboard = "Client POS/IHSS Dashboard"
    private const val PermissionServiceNoteModification = "Service Note Modification"
    private const val PermissionScheduleNoteModification = "Schedule Note Modification"
    private const val PermissionScheduleObjectiveModification = "Schedule Objective Modification"
    private const val PermissionScheduleMileageModification = "Schedule Mileage Modification"
    private const val PermissionTrackAttendance = "Track Attendance"
    private const val PermissionQSClockInfo = "QSClock Info"


    private const val ModuleDashboard = "Dashboard"

    private const val ActionRead = "Read"
    private const val ActionAccess = "Access"
    private const val ActionUpdate = "Update"
    private const val ActionDelete = "Delete"
    private const val ActionCreate = "Create"
    private const val ActionAdmin = "Admin"

    var isAdmin = false

    private fun permissions(): List<UserPermission> {
        return Preferences.instance?.user?.userPermissions ?: listOf()
    }

    fun hasPermission(permission: String, action: String): Boolean {
        permissions().map {
            if (it.permissionName == permission && it.permissionAction == action) {
                return true
            }
        }
        return false
    }

    fun hasQSMessagesAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionQSMessages && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasAnnouncementsPermission(): Boolean {
        permissions().map {
            if (it.permissionModule == ModuleDashboard && it.permissionName == PermissionAnnouncements && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasClientModuleAdminPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientModule && it.permissionAction == ActionAdmin) {
                return true
            }
        }
        return false
    }

    fun hasClientModuleAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientModule && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasEmployeeModuleAdminPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionEmployeeModule && it.permissionAction == ActionAdmin) {
                return true
            }
        }
        return false
    }

    fun hasEmployeeModuleAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionEmployeeModule && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasScheduleModuleAdminPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionScheduleModule && it.permissionAction == ActionAdmin) {
                return true
            }
        }
        return false
    }

    fun hasScheduleModificationCreatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionScheduleModification && it.permissionAction == ActionCreate) {
                return true
            }
        }
        return false
    }

    fun hasScheduleModificationUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionScheduleModification && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasScheduleModificationDeletePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionScheduleModification && it.permissionAction == ActionDelete) {
                return true
            }
        }
        return false
    }

    fun hasEmployeeScheduleViewReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionEmployeeScheduleView && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasEmployeeScheduleViewAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionEmployeeScheduleView && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasClientScheduleViewReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientScheduleView && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasClientScheduleViewAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientScheduleView && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasEmployeeOTDashboardReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionEmployeeOTDashboard && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasEmployeeOTDashboardAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionEmployeeOTDashboard && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasViewTrainedClientsOnlyReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewTrainedClientsOnly && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasViewTrainedClientsOnlyAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewTrainedClientsOnly && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasViewTrainedClientsOnlyUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewTrainedClientsOnly && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasViewOfficeClientsOnlyReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeClientsOnly && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasViewOfficeClientsOnlyAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeClientsOnly && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasViewOfficeClientsOnlyUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeClientsOnly && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasViewClientCaseloadOfAssignedSupervisorOnlyReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewClientCaseloadOfAssignedSupervisorOnly && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasViewClientCaseloadOfAssignedSupervisorOnlyAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewClientCaseloadOfAssignedSupervisorOnly && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasViewClientCaseloadOfAssignedSupervisorOnlyUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewClientCaseloadOfAssignedSupervisorOnly && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasEmployeeUserRelatedInfoOnlyReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionEmployeeUserRelatedInfoOnly && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasEmployeeUserRelatedInfoOnlyUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionEmployeeUserRelatedInfoOnly && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasEmployeeUserRelatedInfoOnlyAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionEmployeeUserRelatedInfoOnly && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasViewOfficeEmployeesOnlyReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeEmployeesOnly && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasViewOfficeEmployeesOnlyUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeEmployeesOnly && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasViewOfficeEmployeesOnlyAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeEmployeesOnly && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasViewOfficeEmployeesOnlyCreatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeEmployeesOnly && it.permissionAction == ActionCreate) {
                return true
            }
        }
        return false
    }

    fun hasViewOfficeEmployeesOnlyDeletePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeEmployeesOnly && it.permissionAction == ActionDelete) {
                return true
            }
        }
        return false
    }

    fun hasViewUserScheduleOnlyReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewUserScheduleOnly && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasViewUserScheduleOnlyUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewUserScheduleOnly && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasViewUserScheduleOnlyAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewUserScheduleOnly && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasViewUserScheduleOnlyCreatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewUserScheduleOnly && it.permissionAction == ActionCreate) {
                return true
            }
        }
        return false
    }

    fun hasViewUserScheduleOnlyDeletePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewUserScheduleOnly && it.permissionAction == ActionDelete) {
                return true
            }
        }
        return false
    }

    fun hasViewSameSupervisedEmployeesAsUserReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewSameSupervisedEmployeesAsUser && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasViewSameSupervisedEmployeesAsUserUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewSameSupervisedEmployeesAsUser && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasViewSameSupervisedEmployeesAsUserAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewSameSupervisedEmployeesAsUser && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasViewSameSupervisedEmployeesAsUserCreatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewSameSupervisedEmployeesAsUser && it.permissionAction == ActionCreate) {
                return true
            }
        }
        return false
    }

    fun hasViewSameSupervisedEmployeesAsUserDeletePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewSameSupervisedEmployeesAsUser && it.permissionAction == ActionDelete) {
                return true
            }
        }
        return false
    }

    fun hasPriorCurrentNextMonthViewOnlyReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionPriorCurrentNextMonthViewOnly && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasClientPOSIHSSDashboardAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientPOSIHSSDashboard && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasServiceNoteModificationUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionServiceNoteModification && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasScheduleNoteModificationUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionScheduleNoteModification && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasScheduleObjectiveModificationUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionScheduleObjectiveModification && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasScheduleMileageModificationUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionScheduleMileageModification && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasTrackAttendanceAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionTrackAttendance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasQSClockInfoAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionQSClockInfo && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasQSClockInfoUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionQSClockInfo && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    /*----------------------------------------- Preeti -----------------------------------------*/

    fun hasPermissionViewOfficeEmployeesOnlyAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeEmployeesOnly && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionViewOfficeEmployeesOnlyRead(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeEmployeesOnly && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasPermissionViewOfficeEmployeesOnlyUpdate(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewOfficeEmployeesOnly && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientContactsMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientContactsMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientContactsMaintenanceRead(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientContactsMaintenance && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientContactsMaintenanceUpdate(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientContactsMaintenance && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientContactsMaintenanceCreate(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientContactsMaintenance && it.permissionAction == ActionCreate) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientDoctorVisitMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientDoctorVisitMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientDoctorVisitMaintenanceCreate(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientDoctorVisitMaintenance && it.permissionAction == ActionCreate) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientDoctorVisitMaintenanceUpdate(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientDoctorVisitMaintenance && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientDoctorVisitMaintenanceRead(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientDoctorVisitMaintenance && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientMedRxMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientMedRxMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientMedRxMaintenanceUpdate(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientMedRxMaintenance && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientMedRxMaintenanceCreate(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientMedRxMaintenance && it.permissionAction == ActionCreate) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientAllergiesMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientAllergiesMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientPOSMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientPOSMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientISPMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientISPMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientEAPMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientEAPMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientFIRMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientFIRMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientConnectMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientConnectMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientQualityAssuranceMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientQualityAssuranceMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientDocumentMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientDocumentMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientTrainingGuideMaintenanceAccess(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientTrainingGuideMaintenance && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientInfoMaintenanceUpdate(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientInfoMaintenance && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }

    fun hasPermissionClientSSNMaintenanceRead(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionClientSSNMaintenance && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasViewSameSupervisedEmployeesUserAccessPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewSameSupervisedEmployeesUser && it.permissionAction == ActionAccess) {
                return true
            }
        }
        return false
    }

    fun hasViewSameSupervisedEmployeesUserReadPermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewSameSupervisedEmployeesUser && it.permissionAction == ActionRead) {
                return true
            }
        }
        return false
    }

    fun hasViewSameSupervisedEmployeesUserUpdatePermission(): Boolean {
        permissions().map {
            if (it.permissionName == PermissionViewSameSupervisedEmployeesUser && it.permissionAction == ActionUpdate) {
                return true
            }
        }
        return false
    }
}