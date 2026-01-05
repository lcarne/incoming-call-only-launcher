package com.callonly.launcher.data.repository

import com.callonly.launcher.data.model.CallLog
import kotlinx.coroutines.flow.Flow

interface CallLogRepository {
    fun getAllCallLogs(): Flow<List<CallLog>>
    suspend fun insertCallLog(callLog: CallLog)
    suspend fun clearHistory()
}
