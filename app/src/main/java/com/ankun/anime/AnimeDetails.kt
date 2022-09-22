package com.ankun.anime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ankun.R

class AnimeDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_details)

        val animeScrape = AnimeScrape(this)
        animeScrape.GetAnimeDetails(intent.extras!!.getString("anime_id")!!).execute()

        val animeGenreRv: RecyclerView = findViewById(R.id.genre_list)
        animeGenreRv.layoutManager = GridLayoutManager(this, 3)

        val episodesRv: RecyclerView = findViewById(R.id.episode_list)
        episodesRv.layoutManager = GridLayoutManager(this, 2)
    }
}