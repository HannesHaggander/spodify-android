package com.towerowl.spodify.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.towerowl.spodify.R
import com.towerowl.spodify.data.api.Show
import com.towerowl.spodify.ext.enqueue
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.view_holder_show_item.view.*

class HomeFragment : Fragment() {

    private val showRecyclerAdapter: ShowRecyclerAdapter by lazy { ShowRecyclerAdapter() }

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
        App.instance().repo.spotifyRepository().userLibrary().shows().enqueue(
            onResponse = { call, response ->
                showRecyclerAdapter.data = response.body()
                    ?.items
                    .orEmpty()
                    .map { ShowDataItem(it.show) }
            },
            onFailure = { call, error ->
                1
            }
        )
    }

    private fun setupRecycler() {
        with(home_show_recycler) {
            adapter = showRecyclerAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }
}

class ShowRecyclerAdapter() : RecyclerView.Adapter<ShowRecyclerViewModel>() {
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
            holder.itemView.show_item_description.text = show.description
        }
    }

    override fun getItemCount(): Int = data.size

}

class ShowRecyclerViewModel(v: View) : RecyclerView.ViewHolder(v)

data class ShowDataItem(val show: Show)