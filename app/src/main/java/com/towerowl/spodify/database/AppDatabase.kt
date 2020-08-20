package com.towerowl.spodify.database

import android.content.Context
import androidx.room.*
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

        fun create(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
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

