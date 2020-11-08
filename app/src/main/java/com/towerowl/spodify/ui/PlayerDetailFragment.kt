package com.towerowl.spodify.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.types.PlayerState
import com.towerowl.spodify.R
import com.towerowl.spodify.ext.millisToSec
import com.towerowl.spodify.ext.secondsToMillis
import com.towerowl.spodify.ext.secondsToReadable
import com.towerowl.spodify.misc.App
import kotlinx.android.synthetic.main.fragment_player_detail.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.time.seconds

class PlayerDetailFragment : Fragment() {

    private var progressJob: Job? = null
        set(value) {
            field?.cancel("starting another timer")
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_player_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateImage()
        App.instance().spotifyAppRemote
            ?.playerApi
            ?.subscribeToPlayerState()
            ?.setEventCallback { onPlayerStateUpdated(it) }
    }

    private fun updateImage() {
        App.instance().spotifyAppRemote?.run {
            playerApi.playerState?.setResultCallback { result ->
                imagesApi.getImage(result.track.imageUri)?.run {
                    setResultCallback { img ->
                        img?.run {
                            Glide.with(requireView()).load(this).into(player_detail_image)
                        }
                    }
                }
            }
        }
    }

    private fun onPlayerStateUpdated(playerState: PlayerState) {
        with(playerState.track) {
            if (!isPodcast) return
            player_detail_title.text = this.name
        }

        player_detail_play_pause.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (playerState.isPaused) R.drawable.ic_play_circle else R.drawable.ic_pause_circle
            )
        )
        player_detail_play_pause.setOnClickListener {
            App.instance().spotifyAppRemote?.playerApi?.run {
                if (playerState.isPaused) resume() else pause()
            }
        }

        player_detail_total_time.text =
            playerState.track.duration.millisToSec().toInt().secondsToReadable()
        player_detail_current_time.text =
            playerState.playbackPosition.millisToSec().toInt().secondsToReadable()

        player_detail_progress_bar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekbar: SeekBar?) {
                progressJob?.cancel("seek bar touch initiated")
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
                App.instance().spotifyAppRemote?.playerApi?.run {
                    if (seekbar == null) return@run
                    seekTo(seekbar.progress.toLong().secondsToMillis())
                }
                if (!playerState.isPaused) startProgressTimer()
            }

        })

        player_detail_progress_bar.max = playerState.track.duration.millisToSec().toInt()
        player_detail_progress_bar.progress = playerState.playbackPosition.millisToSec().toInt()
        if (playerState.isPaused) progressJob?.cancel("paused")
        else startProgressTimer()
    }

    private fun startProgressTimer() {
        lifecycleScope.launch(IO) {
            while (isActive) {
                withContext(Main) {
                    player_detail_progress_bar.progress += PROGRESS_INCREMENT
                    player_detail_current_time.text =
                        player_detail_progress_bar.progress.secondsToReadable()
                }
                delay(SECOND_DELAY)
            }
        }.also { progressJob = it }
    }

    companion object {
        private const val PROGRESS_INCREMENT = 1
        private const val SECOND_DELAY = 1000L
        private const val IMAGE_ROUNDED_CORNERS = 20
    }
}