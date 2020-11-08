package com.towerowl.spodify.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.DialogFragmentNavigatorDestinationBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.towerowl.spodify.R
import com.towerowl.spodify.data.api.SearchResults
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.view_holder_search_episode.view.*
import kotlinx.android.synthetic.main.view_holder_search_show.view.*

class SearchFragment : Fragment() {

    private val resultsAdapter by lazy {
        SearchResultsAdapter {
            Log.d(TAG, "On item click: Pressed item: $it")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchResultsListener()
        setupRecycler()
        App.instance()
            .viewModels
            .searchViewModel()
            .search("anders och måns", 0)
    }

    private fun setupRecycler() {
        search_results_recycler.apply {
            adapter = resultsAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    private fun setupSearchResultsListener() {
        App.instance()
            .viewModels
            .searchViewModel()
            .searchResults
            .observe(viewLifecycleOwner) { results ->
                mutableListOf<SearchResultsItem>().apply {
                    addAll(results.episodes?.items?.map { episode ->
                        SearchResultsItem(
                            href = episode.href,
                            title = episode.name,
                            imageUrl = episode.images.firstOrNull()?.url,
                            type = SearchType.EPISODE
                        )
                    }.orEmpty())
                    addAll(
                        results.shows?.items?.map { show ->
                            SearchResultsItem(
                                href = show.href,
                                title = show.name,
                                imageUrl = show.images.firstOrNull()?.url,
                                type = SearchType.SHOW
                            )
                        }.orEmpty()
                    )
                }.run { resultsAdapter.items = this }
            }
    }

    companion object {
        private const val TAG = "SearchFragment"
    }
}

class SearchResultsAdapter(private val onItemClick: (SearchResultsItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<SearchResultsItem> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        LayoutInflater.from(parent.context).let { inflater ->
            when (viewType) {
                SearchType.SHOW.ordinal -> {
                    inflater.inflate(R.layout.view_holder_search_show, parent, false)
                        .run { return SearchResultShowViewHolder(this) }
                }
                SearchType.EPISODE.ordinal -> {
                    inflater.inflate(R.layout.view_holder_search_episode, parent, false)
                        .run { return SearchResultEpisodeViewHolder(this) }
                }
                else -> throw(Exception("Unhandled search type ordinal"))
            }
        }
    }

    override fun getItemViewType(position: Int): Int = items[position].type.ordinal

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].run {
            holder.itemView.setOnClickListener { onItemClick(this) }
            when (holder) {
                is SearchResultShowViewHolder -> holder.setData(this)
                is SearchResultEpisodeViewHolder -> holder.setData(this)
                else -> throw Exception("Unexpected search result view holder")
            }
        }
    }
}

class SearchResultShowViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    fun setData(searchResultsItem: SearchResultsItem) {
        with(itemView) {
            searchResultsItem.imageUrl?.let { image ->
                Glide.with(this).load(image).into(search_show_image)
            }
            search_show_text.text = searchResultsItem.title
        }
    }
}

class SearchResultEpisodeViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    fun setData(searchResultsItem: SearchResultsItem) {
        with(itemView) {
            searchResultsItem.imageUrl?.let { image ->
                Glide.with(this).load(image).into(search_episode_image)
            }
            search_episode_text.text = searchResultsItem.title
        }
    }
}

data class SearchResultsItem(
    val href: String,
    val title: String,
    val imageUrl: String?,
    val type: SearchType
)

enum class SearchType {
    SHOW,
    EPISODE
}