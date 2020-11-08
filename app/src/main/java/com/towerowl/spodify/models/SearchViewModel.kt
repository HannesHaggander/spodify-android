package com.towerowl.spodify.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.towerowl.spodify.data.api.SearchResults
import com.towerowl.spodify.ext.enqueue
import com.towerowl.spodify.repositories.ContentRetriever
import com.towerowl.spodify.repositories.requests.Search
import retrofit2.Call

class SearchViewModel(private val contentRetriever: ContentRetriever) : ViewModel() {
    private val _searchResults = MutableLiveData<SearchResults>()
    val searchResults: LiveData<SearchResults> get() = _searchResults
    private var searchCall: Call<SearchResults>? = null
        set(value) {
            field?.cancel()
            field = value
        }

    fun search(query: String, offset: Int) {
        contentRetriever.search().query(
            mapOf(
                Search.QUERY_QUERY to query,
                Search.QUERY_OFFSET to offset.toString(),
                Search.QUERY_TYPE_DEFAULT,
                Search.QUERY_LIMIT_DEFAULT,
            )
        ).also {
            searchCall = it
        }.enqueue(
            onResponse = { call, response ->
                if (response.isSuccessful) {
                    response.body()?.run { _searchResults.postValue(this) }
                }
            },
            onFailure = { call, exception ->
                Log.w(TAG, "search: Failed to get search results", exception)
            }
        )
    }

    companion object {
        private const val TAG = "SearchViewModel"
    }
}