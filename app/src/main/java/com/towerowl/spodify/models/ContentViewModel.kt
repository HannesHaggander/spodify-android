package com.towerowl.spodify.models

import androidx.lifecycle.ViewModel
import com.towerowl.spodify.data.api.ShowEpisodes
import com.towerowl.spodify.ext.enqueue
import com.towerowl.spodify.repositories.ContentRetriever
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ContentViewModel(private val contentRetriever: ContentRetriever) : ViewModel() {

    suspend fun getShows(id: String) = suspendCoroutine<ShowEpisodes?> { s ->
        contentRetriever.shows()
            .shows(id)
            .enqueue(
                onResponse = { _, request ->
                    s.resume(request.body())
                },
                onFailure = { _, err ->
                    s.resumeWithException(Exception(err.message))
                }
            )
    }

}