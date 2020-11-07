package com.towerowl.spodify.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.towerowl.spodify.data.api.Episode
import com.towerowl.spodify.ext.enqueue
import com.towerowl.spodify.repositories.ContentRetriever
import com.towerowl.spodify.repositories.requests.Search
import com.towerowl.spodify.repositories.requests.Shows

class ShowViewModel(private val contentRetriever: ContentRetriever) : ViewModel() {

    companion object {
        private const val TAG = "ShowViewModel"
    }

    private val mEpisodes = MutableLiveData<List<Episode>>()
    val episodes: LiveData<List<Episode>> get() = mEpisodes

    fun clearEpisodes() {
        mEpisodes.postValue(listOf())
    }

    fun getShowEpisodes(showId: String, offset: Int = 0) {
        contentRetriever.shows()
            .shows(
                showId,
                mapOf(Shows.QUERY_LIMIT_DEFAULT, Shows.QUERY_OFFSET to offset.toString())
            ).enqueue(
                onResponse = { c, request ->
                    request.body()?.also { shows ->
                        if (mEpisodes.value == null) {
                            mEpisodes.postValue(shows.items.toMutableList())
                        } else {
                            mEpisodes.postValue(mEpisodes.value?.plus(shows.items))
                        }
                    } ?: Log.w(TAG, "getShowEpisodes: Missing body from request")
                },
                onFailure = { c, err ->
                    Log.w(TAG, "getShowEpisodes: Failed to get episodes", Exception(err))
                }
            )
    }

}