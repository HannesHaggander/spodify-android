package com.towerowl.spodify.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.towerowl.spodify.R
import com.towerowl.spodify.data.api.Episode
import com.towerowl.spodify.data.api.Show
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_show_detail.*
import kotlinx.android.synthetic.main.view_holder_episode_item.view.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowOverviewFragment : Fragment() {

    private val episodeAdapter: EpisodeAdapter by lazy {
        EpisodeAdapter(
            onItemClick = { episode ->
                App.instance().spotifyAppRemote?.playerApi?.play(episode.uri)
            },
            onPlayClick = { episode ->
                App.instance().spotifyAppRemote?.playerApi?.play(episode.uri)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_show_detail, container, false)

    private val show: Show by lazy {
        arguments?.getParcelable<Show>(Show::class.java.simpleName) ?: throw Exception()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(show.images.first().url).into(show_overview_cover)
        setupRecycler()
        registerObserver()
    }

    private fun setupRecycler() {
        with(show_overview_recycler) {
            adapter = episodeAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    private fun registerObserver() {
        lifecycleScope.launch(IO) {
            App.instance()
                .viewModels
                .showViewModel()
                .getShowEpisodes(show.id)
                .run {
                    withContext(Main) {
                        observe(viewLifecycleOwner, { shows ->
                            episodeAdapter.data = shows.items
                        })
                    }
                }
        }
    }

    inner class EpisodeAdapter(
        private val onItemClick: (Episode) -> Unit,
        private val onPlayClick: (Episode) -> Unit
    ) : RecyclerView.Adapter<EpisodeViewHolder>() {

        var data: List<Episode> = listOf()
            set(value) {
                field = value.sortedByDescending { it.releaseDate }
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_holder_episode_item, parent, false)
                .run { return EpisodeViewHolder((this)) }
        }

        override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
            with(data[position]) {
                holder.itemView.also { v ->
                    v.view_holder_episode_release_date.text = releaseDate
                    v.view_holder_episode_name.text = name
                    v.view_holder_episode_description.text = description
                    v.view_holder_episode_play_pause.setOnClickListener { onPlayClick(this) }
                    v.setOnClickListener { onItemClick(this) }
                }
            }
        }

        override fun getItemCount(): Int = data.size

    }

    inner class EpisodeViewHolder(v: View) : RecyclerView.ViewHolder(v)

}
