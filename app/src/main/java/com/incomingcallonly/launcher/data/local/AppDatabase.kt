package com.incomingcallonly.launcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.incomingcallonly.launcher.data.model.CallLog
import com.incomingcallonly.launcher.data.model.Contact

@Database(entities = [Contact::class, CallLog::class], version = 3, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun callLogDao(): CallLogDao
}
