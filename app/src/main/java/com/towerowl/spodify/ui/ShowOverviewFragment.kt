package com.towerowl.spodify.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState
import com.towerowl.spodify.R
import com.towerowl.spodify.data.api.Episode
import com.towerowl.spodify.data.api.Show
import com.towerowl.spodify.ext.asVisibility
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_show_detail.*
import kotlinx.android.synthetic.main.view_holder_episode_item.view.*

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

    private val show: Show by lazy {
        arguments?.getParcelable<Show>(Show::class.java.simpleName) ?: throw Exception()
    }

    private var playerStateSubscription: Subscription<PlayerState>? = null
        set(value) {
            field?.cancel()
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_show_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(show.images.first().url).into(show_overview_cover)
        setupRecycler()
        registerEpisodesObserver()
        App.instance().viewModels.showViewModel().run {
            clearEpisodes()
            getShowEpisodes(show.id)
        }
    }

    override fun onStart() {
        super.onStart()
        registerPlayStateObserver()
    }

    override fun onStop() {
        super.onStop()
        playerStateSubscription?.cancel()
    }

    private fun setupRecycler() {
        with(show_overview_recycler) {
            adapter = episodeAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1)
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        App.instance()
                            .viewModels
                            .showViewModel()
                            .getShowEpisodes(show.id, episodeAdapter.itemCount)
                    }
                }
            })
        }
    }

    private fun registerEpisodesObserver() {
        App.instance().viewModels.showViewModel().episodes.observe(viewLifecycleOwner) { shows ->
            episodeAdapter.data = shows.toMutableList()
        }
    }

    private fun registerPlayStateObserver() {
        App.instance().spotifyAppRemote?.run {
            playerApi.subscribeToPlayerState().setEventCallback { state ->
                episodeAdapter.currentlyPlaying = state.track.uri
            }.also { playerStateSubscription = it }
        }
    }

    inner class EpisodeAdapter(
        private val onItemClick: (Episode) -> Unit,
        private val onPlayClick: (Episode) -> Unit
    ) : RecyclerView.Adapter<EpisodeViewHolder>() {

        var currentlyPlaying: String? = null
            set(value) {
                field?.run {
                    data.indexOfFirst { episode -> episode.uri == this }.also { index ->
                        if (index < 0) return@run
                        notifyItemChanged(index)
                    }
                }
                data.indexOfFirst { episode -> episode.uri == value }.also { index ->
                    if (index < 0) return@also
                    notifyItemChanged(index)
                }
                field = value
            }

        var data: MutableList<Episode> = mutableListOf()
            set(value) {
                value.sortByDescending { it.releaseDate }
                field = value
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
                    ContextCompat.getColor(
                        requireContext(),
                        if (currentlyPlaying == this.uri) R.color.colorPrimary else R.color.white
                    ).also { color -> v.view_holder_episode_name.setTextColor(color) }
                    v.view_holder_episode_playing.visibility =
                        (currentlyPlaying == this.uri).asVisibility()
                    v.view_holder_episode_play_pause.setOnClickListener { onPlayClick(this) }
                    v.setOnClickListener { onItemClick(this) }
                }
            }
        }

        override fun getItemCount(): Int = data.size

    }

    inner class EpisodeViewHolder(v: View) : RecyclerView.ViewHolder(v)
}
