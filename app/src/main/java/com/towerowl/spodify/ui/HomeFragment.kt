package com.towerowl.spodify.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.towerowl.spodify.R
import com.towerowl.spodify.data.api.Show
import com.towerowl.spodify.ext.asVisibility
import com.towerowl.spodify.ext.enqueue
import com.towerowl.spodify.ext.setGone
import com.towerowl.spodify.ext.setVisible
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.view_holder_show_item.view.*

class HomeFragment : Fragment() {

    private val showRecyclerAdapter: ShowRecyclerAdapter by lazy {
        ShowRecyclerAdapter { clicked ->
            findNavController().navigate(
                R.id.nav_home_to_podcast_detail,
                bundleOf(Show::class.java.simpleName to clicked.show)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
    }

    override fun onStart() {
        super.onStart()
        getContent()
    }

    private fun getContent() {
        loadingStarted()
        App.instance().repo.spotifyRepository().userLibrary().shows().enqueue(
            onResponse = { call, response ->
                showRecyclerAdapter.data = response.body()
                    ?.items
                    .orEmpty()
                    .map { ShowDataItem(it.show) }
                    .also {
                        home_no_shows_text.visibility = it.isEmpty().asVisibility()
                        loadingFinished()
                    }
            },
            onFailure = { call, error ->
                1
            }
        )
    }

    private fun setupRecycler() {
        with(home_show_recycler) {
            adapter = showRecyclerAdapter
            layoutManager = GridLayoutManager(
                requireContext(),
                2,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    private fun loadingStarted() {
        home_loading.setVisible()
        home_no_shows_text.setGone()
    }

    private fun loadingFinished() {
        home_loading.setGone()
    }
}

class ShowRecyclerAdapter(val onClick: (ShowDataItem) -> Unit) :
    RecyclerView.Adapter<ShowRecyclerViewModel>() {
    var data = listOf<ShowDataItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowRecyclerViewModel =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_show_item, parent, false)
            .run { ShowRecyclerViewModel(this) }

    override fun onBindViewHolder(holder: ShowRecyclerViewModel, position: Int) {
        with(data[position]) {
            Glide.with(holder.itemView)
                .load(show.images.first().url)
                .into(holder.itemView.show_item_image)
            holder.itemView.show_item_name.text = show.name
            holder.itemView.show_item_publisher.text = show.publisher
            holder.itemView.setOnClickListener { onClick(this) }
        }
    }

    override fun getItemCount(): Int = data.size

}

class ShowRecyclerViewModel(v: View) : RecyclerView.ViewHolder(v)

data class ShowDataItem(val show: Show)