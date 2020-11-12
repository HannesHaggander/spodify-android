package com.towerowl.spodify.repositories

import androidx.lifecycle.LiveData
import com.towerowl.spodify.data.QueueDao
import com.towerowl.spodify.data.QueueData

class QueueRepository(private val queueDao: QueueDao) {
    fun addToQueue(url: String) = queueDao.insert(QueueData(url = url))

    fun removeFromQueue(uri: String) = queueDao.deleteByUrl(uri)

    fun getQueueLive(): LiveData<List<QueueData>> = queueDao.getQueueLive()
}