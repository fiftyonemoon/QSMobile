package com.quicksolveplus.qspmobile.schedule.models

import com.google.gson.annotations.SerializedName

data class PreferenceScheduleItem(
    @SerializedName("Comments") val comments: String?,
    @SerializedName("Create_DateTime") val createDateTime: Any?,
    @SerializedName("Create_UserUID") val createUserUID: Any?,
    @SerializedName("dDay") val dDay: Any?,
    @SerializedName("dMonth") val dMonth: Any?,
    @SerializedName("dYear") val dYear: Any?,
    @SerializedName("EndOnDate") val endOnDate: String?,
    @SerializedName("EndTime") val endTime: String?,
    @SerializedName("EndTimeActual") val endTimeActual: String?,
    @SerializedName("Hours") val hours: Double,
    @SerializedName("Modify_DateTime") val modifyDateTime: Any?,
    @SerializedName("Modify_UserUID") val modifyUserUID: Any?,
    @SerializedName("MonthDate") val monthDate: Any?,
    @SerializedName("PreferenceDetail") val preferenceDetail: String?,
    @SerializedName("PreferenceType") val preferenceType: String?,
    @SerializedName("RecordType") val recordType: Any?,
    @SerializedName("RecurrenceRule") val recurrenceRule: Any?,
    @SerializedName("RecurrenceType") val recurrenceType: Any?,
    @SerializedName("SchedDate") val schedDate: String?,
    @SerializedName("SchedDateEnd") val schedDateEnd: String?,
    @SerializedName("ShiftTopValue") val shiftTopValue: Double,
    @SerializedName("StartTime") val startTime: String?,
    @SerializedName("StartTimeActual") val startTimeActual: String?,
    @SerializedName("Status") val status: String?,
    @SerializedName("UID") val uID: Int,
    @SerializedName("UsersUID") val usersUID: Int,
    @SerializedName("WeekDays") val weekDays: Any?,
    @SerializedName("WeekPosition") val weekPosition: Any?,
    @SerializedName("WorkPref1") val workPref1: Any?,
    var topMargin: Double?,
    var buttonHeight: Int?,
    var buttonBackground: String?,
    var buttonForeground: String?,
    var title: String?,
    var tempStartTime: String?,
    var tempEndTime: String?,
    var tempSchedDate: String?,
    var tempSchedDateEnd: String?,
) : java.io.Serializable, Cloneable {

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }
}