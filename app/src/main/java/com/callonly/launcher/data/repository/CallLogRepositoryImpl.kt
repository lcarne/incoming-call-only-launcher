package com.callonly.launcher.data.repository

import com.callonly.launcher.data.local.CallLogDao
import com.callonly.launcher.data.model.CallLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CallLogRepositoryImpl @Inject constructor(
    private val callLogDao: CallLogDao
) : CallLogRepository {
    override fun getAllCallLogs(): Flow<List<CallLog>> = callLogDao.getAllCallLogs()

    override suspend fun insertCallLog(callLog: CallLog) {
        callLogDao.insert(callLog)
    }

    override suspend fun clearHistory() {
        callLogDao.clearAll()
    }
}
