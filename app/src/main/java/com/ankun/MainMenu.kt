package com.ankun

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import com.ankun.anime.AnimeScrape

class MainMenu : AppCompatActivity() {
    private val animeScrape = AnimeScrape(this)
    private lateinit var recentUpdatesButton: AppCompatButton
    private lateinit var seasonalButton: AppCompatButton
    private lateinit var mostPopularButton: AppCompatButton
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        recentUpdatesButton = findViewById(R.id.recent_update_button)
        seasonalButton = findViewById(R.id.seasonal_release_button)
        mostPopularButton = findViewById(R.id.popular_release_button)
        searchView = findViewById(R.id.search_view)

        initTopToolbar()

        // "Recent Updates" onclick listener
        // go to new activity to show anime list
        recentUpdatesButton.setOnClickListener {
            startActivity(Intent(it.context, LatestListSubbed::class.java))
        }

        // "Most Popular" onclick listener
        // go to new activity to show most popular list (year)
        mostPopularButton.setOnClickListener {
            startActivity(Intent(it.context, MostPopularYearly::class.java))
        }
    }

    private fun initTopToolbar() {
        // change search view color
        val searchEditText: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        // change search view inputted text
        searchEditText.setTextColor(Color.WHITE)
        // change search view hint text
        searchEditText.hint = resources.getString(R.string.search_hint)
        searchEditText.setHintTextColor(Color.LTGRAY)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(applicationContext, SearchResultList::class.java)
                intent.putExtra("SEARCH_TEXT", query!!.trim())
                startActivity(intent)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }
}