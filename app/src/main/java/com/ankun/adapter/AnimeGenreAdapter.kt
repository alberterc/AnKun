package com.ankun.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ankun.R

class AnimeGenreAdapter: RecyclerView.Adapter<AnimeGenreAdapter.ViewHolder> {
    private var context: Context?
    private var genreList = emptyList<Map<String, String>>()
    
    constructor(context: Context, genreList: List<Map<String, String>>) {
        this.context = context
        this.genreList = genreList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(genre: Map<String, String>) {
            animeGenre.text = genre["genreType"]
            animeGenreCard.setOnClickListener {
                Toast.makeText(it.context, genre["genreURL"], Toast.LENGTH_SHORT)
                    .show()
            }
        }

        private val animeGenre: TextView = itemView.findViewById(R.id.anime_genre)
        private val animeGenreCard: CardView = itemView.findViewById(R.id.anime_genre_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(genreList[position])
    }

    override fun getItemCount(): Int {
        return genreList.size
    }
}