package com.towerowl.spodify.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.towerowl.spodify.data.api.ShowEpisodes
import com.towerowl.spodify.ext.enqueue
import com.towerowl.spodify.repositories.ContentRetriever
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ShowViewModel(private val contentRetriever: ContentRetriever) : ViewModel() {

    private val showDataMap = MutableLiveData<MutableMap<String, MutableLiveData<ShowEpisodes>>>()
        get() {
            if (field.value == null) synchronized(this) {
                viewModelScope.launch(Main) {
                    field.value = mutableMapOf()
                }
            }
            return field
        }

    suspend fun getShowEpisodes(showId: String) = suspendCoroutine<LiveData<ShowEpisodes>> { s ->
        if (showId.isEmpty()) {
            s.resumeWithException(Exception("Show id can not be empty"))
            return@suspendCoroutine
        }

        showDataMap.value?.get(showId)?.run {
            s.resume(this)
            return@suspendCoroutine
        }

        contentRetriever.shows().shows(showId).enqueue(
            onResponse = { _, request ->
                request.body()?.also { shows ->
                    showDataMap.value?.run {
                        with(MutableLiveData(shows)) {
                            this@run[showId] = this
                            s.resume(this)
                        }
                    }
                } ?: s.resumeWithException(Exception("Failed to get spotify show id"))
            },
            onFailure = { _, err ->
                s.resumeWithException(err)
            }
        )
    }

}