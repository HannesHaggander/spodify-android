package com.towerowl.spodify.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.towerowl.spodify.R
import com.towerowl.spodify.data.api.Show
import com.towerowl.spodify.ext.enqueue
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_podcast_detail.*

class PodcastDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_podcast_detail, container, false)

    private val show: Show by lazy {
        arguments?.getParcelable<Show>(Show::class.java.simpleName) ?: throw Exception()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(show.images.first().url).into(podcast_detail_cover)
        getShows()
    }

    private fun getShows() {
        App.instance()
            .repo
            .spotifyRepository()
            .shows()
            .shows(show.id)
            .enqueue(
                onResponse = { _, request ->
                    request.body()?.also {

                    }
                },
                onFailure = { _, _ ->

                }
            )
    }

}