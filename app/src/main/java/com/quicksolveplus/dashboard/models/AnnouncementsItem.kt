package com.quicksolveplus.dashboard.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AnnouncementsItem(
    @SerializedName("AnnouncementId")
    val announcementId: Int,
    @SerializedName("AnnouncementMessage")
    val announcementMessage: String,
    @SerializedName("AnnouncementReadDateTime")
    val announcementReadDateTime: String,
    @SerializedName("AnnouncementSubject")
    val announcementSubject: String,
    @SerializedName("IsArchived")
    val isArchived: Boolean,
    @SerializedName("IsHighPriorityAnnouncement")
    val isHighPriorityAnnouncement: Boolean,
    @SerializedName("IsRead")
    val isRead: Boolean,
    @SerializedName("SenderId")
    val senderId: Int,
    @SerializedName("SenderName")
    val senderName: String,
    @SerializedName("SentDateTime")
    val sentDateTime: String,
    var isSelected: Boolean = false
) : Serializable