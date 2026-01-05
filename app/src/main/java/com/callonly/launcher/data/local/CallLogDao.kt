package com.callonly.launcher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.callonly.launcher.data.model.CallLog
import kotlinx.coroutines.flow.Flow

@Dao
interface CallLogDao {
    @Insert
    suspend fun insert(callLog: CallLog)

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLog>>

    @Query("DELETE FROM call_logs")
    suspend fun clearAll()
}
