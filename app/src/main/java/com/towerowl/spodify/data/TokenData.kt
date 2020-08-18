package com.towerowl.spodify.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "auth_data")
data class TokenData(
    @PrimaryKey val accessToken: String,
    val expiresIn: Int,
    val createdAt: DateTime = DateTime.now()
)