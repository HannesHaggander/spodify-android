package com.towerowl.spodify.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.towerowl.spodify.ext.toUtc
import org.joda.time.DateTime
import java.util.*

@Entity(tableName = "queue_data")
data class QueueData(
    @PrimaryKey val url: String,
    val createdAtMs: Long = DateTime.now().toUtc().millis
)