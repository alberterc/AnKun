package com.ankun

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ankun.anime.AnimeScrape
import com.google.android.material.appbar.MaterialToolbar

class SearchResultList : AppCompatActivity() {
    private val animeScrape = AnimeScrape(this)
    private var pageNumberInt: Int = 1

    private lateinit var topToolbar: MaterialToolbar
    private lateinit var animeList: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var previousListButton: AppCompatButton
    private lateinit var nextListButton: AppCompatButton
    private lateinit var pageNumberText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result_list)

        topToolbar = findViewById(R.id.top_toolbar)
        animeList = findViewById(R.id.anime_list)
        searchView = findViewById(R.id.search_view)
        previousListButton = findViewById(R.id.previous_button)
        nextListButton = findViewById(R.id.next_button)
        pageNumberText = findViewById(R.id.page_number)

        initTopToolbar()

        // get anime list from search text
        val animeSearchText = intent.extras!!.getString("SEARCH_TEXT")!!
        getAnimeSearchResultList(animeSearchText, "1")

        animeList.layoutManager = GridLayoutManager(this, 2)

        // previous button onclick listener
        // show previous page of anime list
        previousListButton.setOnClickListener {
            if (pageNumberInt > 1) {
                pageNumberInt -= 1
                getAnimeSearchResultList(animeSearchText, pageNumberInt.toString())
            }
        }

        // next button onclick listener
        // show more anime in the list if available
        nextListButton.setOnClickListener {
            pageNumberInt += 1
            getAnimeSearchResultList(animeSearchText, pageNumberInt.toString())
        }
    }

    private fun getAnimeSearchResultList(searchText: String, pageNumber: String) {
        animeScrape.GetAnimeSearchResult(search = searchText, page = pageNumber).execute()
        pageNumberText.text = pageNumber
    }

    private fun initTopToolbar() {
        topToolbar.inflateMenu(R.menu.latest_release_menu)

        // change search view color
        val searchEditText: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        // change search view inputted text
        searchEditText.setTextColor(Color.WHITE)
        // change search view hint text
        searchEditText.hint = resources.getString(R.string.search_hint)
        searchEditText.setHintTextColor(Color.LTGRAY)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                animeScrape.GetAnimeSearchResult(query!!.trim()).execute()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }
}