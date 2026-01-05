package com.callonly.launcher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CallLogType {
    INCOMING_ANSWERED,
    INCOMING_MISSED,
    INCOMING_REJECTED,
    BLOCKED
}

@Entity(tableName = "call_logs")
data class CallLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: String,
    val name: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 0,
    val type: CallLogType
)
