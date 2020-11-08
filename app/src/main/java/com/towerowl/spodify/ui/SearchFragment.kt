package com.towerowl.spodify.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
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
import kotlinx.android.synthetic.main.view_holder_search_title.view.*

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
        setupSearch()
    }

    private fun setupSearch() {
        search_input.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                App.instance()
                    .viewModels
                    .searchViewModel()
                    .search(query.orEmpty(), 0)
                return true
            }
        })
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
                    results.shows?.items?.map { show ->
                        SearchResultsItem(
                            href = show.href,
                            title = show.name,
                            imageUrl = show.images.firstOrNull()?.url,
                            type = SearchType.SHOW
                        )
                    }.orEmpty().also { shows ->
                        if (shows.isEmpty()) return@also
                        SearchResultsItem(
                            title = getString(R.string.shows),
                            type = SearchType.TITLE
                        ).also { add(it) }
                        addAll(shows)
                    }
                    results.episodes?.items?.map { episode ->
                        SearchResultsItem(
                            href = episode.href,
                            title = episode.name,
                            imageUrl = episode.images.firstOrNull()?.url,
                            type = SearchType.EPISODE
                        )
                    }.orEmpty().also { episodes ->
                        if (episodes.isEmpty()) return@also
                        SearchResultsItem(
                            title = getString(R.string.episodes),
                            type = SearchType.TITLE
                        ).also { add(it) }
                        addAll(episodes)
                    }
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
                SearchType.TITLE.ordinal -> {
                    inflater.inflate(R.layout.view_holder_search_title, parent, false)
                        .run { return SearchResultsTitle(this) }
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
                is SearchResultsTitle -> holder.itemView.search_title_text.text = this.title
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

class SearchResultsTitle(v: View) : RecyclerView.ViewHolder(v)

data class SearchResultsItem(
    val href: String = "",
    val title: String,
    val imageUrl: String? = null,
    val type: SearchType
)

enum class SearchType {
    SHOW,
    EPISODE,
    TITLE
}