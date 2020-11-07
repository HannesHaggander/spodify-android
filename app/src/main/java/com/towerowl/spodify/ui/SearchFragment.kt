package com.towerowl.spodify.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.towerowl.spodify.R
import com.towerowl.spodify.misc.App

class SearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchResultsListener()
        App.instance()
            .viewModels
            .searchViewModel()
            .search("anders och måns", 0)
    }

    private fun setupSearchResultsListener() {
        App.instance()
            .viewModels
            .searchViewModel()
            .searchResults
            .observe(viewLifecycleOwner) { results ->
                1
            }
    }
}