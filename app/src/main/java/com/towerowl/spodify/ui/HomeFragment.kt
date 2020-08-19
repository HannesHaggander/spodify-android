package com.towerowl.spodify.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.towerowl.spodify.R
import com.towerowl.spodify.ext.enqueue
import com.towerowl.spodify.misc.App

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onStart() {
        super.onStart()
        getContent()
    }

    private fun getContent() {
        try {
            App.instance().repo.spotifyRepository().userLibrary().shows().enqueue(
                onResponse = { call, response ->
                    1
                },
                onFailure = { call, error ->
                    1
                }
            )
        } catch (e: Exception) {
            1
        }
    }
}