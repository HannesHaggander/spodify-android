package com.towerowl.spodify.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.towerowl.spodify.data.AuthenticationDao
import com.towerowl.spodify.data.TokenData
import com.towerowl.spodify.ext.toUtc
import org.joda.time.DateTime

@Database(
    entities = [TokenData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "spodify-db"
    }

    abstract fun authenticationDao(): AuthenticationDao
}

object DatabaseTypeConverters {

    @JvmStatic
    @TypeConverter
    fun fromDateTime(dateTime: DateTime): Long = dateTime.toUtc().millis

    @JvmStatic
    @TypeConverter
    fun toDateTime(millis: Long): DateTime = DateTime(millis).toLocalDateTime().toDateTime()

}

