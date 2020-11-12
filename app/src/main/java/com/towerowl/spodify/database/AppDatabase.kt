package com.towerowl.spodify.database

import android.content.Context
import androidx.room.*
import com.towerowl.spodify.data.AuthenticationDao
import com.towerowl.spodify.data.QueueDao
import com.towerowl.spodify.data.QueueData
import com.towerowl.spodify.data.TokenData
import com.towerowl.spodify.ext.toUtc
import org.joda.time.DateTime

@Database(
    entities = [TokenData::class, QueueData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authenticationDao(): AuthenticationDao

    abstract fun queueDao(): QueueDao

    companion object {
        const val DATABASE_NAME = "spodify-db"

        fun create(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
}

object DatabaseTypeConverters {

    @JvmStatic
    @TypeConverter
    fun fromDateTime(dateTime: DateTime): Long = dateTime.toUtc().millis

    @JvmStatic
    @TypeConverter
    fun toDateTime(millis: Long): DateTime = DateTime(millis).toLocalDateTime().toDateTime()

}

