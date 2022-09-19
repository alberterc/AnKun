package com.ankun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class LatestList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_list)

        initTopToolbar()

        val animeScrape = AnimeScrape(this)
        animeScrape.GetLatestList().execute()

        val animeList: RecyclerView = findViewById(R.id.anime_list)
        animeList.layoutManager = GridLayoutManager(this, 2)
    }

    private fun initTopToolbar() {
        val topToolbar: MaterialToolbar = findViewById(R.id.top_toolbar)
        topToolbar.inflateMenu(R.menu.latest_release_menu)
    }
}