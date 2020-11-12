package com.towerowl.spodify.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface QueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(queueData: QueueData)

    @Query("SELECT * FROM queue_data where url=:url")
    fun getByUrl(url: String): QueueData?

    @Query("DELETE FROM queue_data WHERE url=:url")
    fun deleteByUrl(url: String)

    @Query("SELECT * FROM queue_data ORDER BY createdAtMs DESC")
    fun getQueueLive(): LiveData<List<QueueData>>
}