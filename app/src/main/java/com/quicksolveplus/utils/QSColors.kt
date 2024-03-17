package com.quicksolveplus.utils

import com.quicksolveplus.qspmobile.schedule.models.PreferenceScheduleItem
import com.quicksolveplus.qspmobile.schedule.models.SchedulesItem
import kotlin.Boolean
import kotlin.String
import kotlin.arrayOf

/**
 * 04/04/23.
 *
 * @author hardkgosai.
 */
object QSColors {

    fun getShiftBackgroundForSchedulePreference(): String {
        return "#F5F5F5"
    }

    fun getShiftForegroundForSchedulePreference(item: PreferenceScheduleItem): String {
        val schedulePreferenceKeyList = arrayOf("Unavailable", "Preferred")
        return if (item.preferenceType?.lowercase() == schedulePreferenceKeyList[0].lowercase()) {
            "#e85454"
        } else {
            "#082a49"
        }
    }

    fun getShiftBackgroundForTOR(): String {
        return "#151515"
    }

    fun getShiftForegroundForTOR(): String {
        return "#ffffff"
    }

    fun getShiftBackground(item: SchedulesItem): String {
        return item.backgroundColor ?: "#FF000C"
    }

    fun getShiftForeground(item: SchedulesItem, isClient: Boolean): String {
        val shiftFirstName: String = if (isClient) item.staffFirstName else item.clientFirstName
        val shiftLastName: String = if (isClient) item.staffLastName else item.clientLastName
        return if (shiftFirstName.isEmpty() && shiftLastName.isEmpty()) {
            "#ffffff"
        } else if (item.foregroundColor.isNotEmpty()) {
            item.foregroundColor
        } else {
            if (item.isCustomEvent) {
                "#ffffff"
            } else {
                "#082a49"
            }
        }
    }
}