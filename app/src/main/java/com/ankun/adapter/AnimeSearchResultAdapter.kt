package com.ankun.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ankun.R
import com.ankun.anime.AnimeDetails
import com.squareup.picasso.Picasso

class AnimeSearchResultAdapter: RecyclerView.Adapter<AnimeSearchResultAdapter.ViewHolder> {
    private var context: Context?
    private var animeList = emptyList<List<String>>()

    constructor(context: Context, animeList: List<List<String>>) {
        this.context = context
        this.animeList = animeList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(anime: List<String>) {
            // get each anime details from anime list
            val title = anime[0].replace("\"", "")
            val animeId = anime[1]
            val thumbnail = anime[2].replace("\"", "")
            val isDub = anime[3] // 0 = sub, 1 = dub

            // create card view for each anime item
            animeTitle.text = title
            Picasso.get()
                .load(thumbnail)
                .into(animeThumbnail)

            // make each anime item clickable to show anime details
            animeCardView.setOnClickListener {
                val intent = Intent(it.context, AnimeDetails::class.java)
                intent.putExtra("anime_id", animeId)
                it.context.startActivity(intent)
            }

            // show dub text if anime is dubbed
            if (isDub == "1") {
                animeIsDub.visibility = View.VISIBLE
            }
            else {
                animeIsDub.visibility = View.GONE
            }
        }

        private val animeTitle: TextView = itemView.findViewById(R.id.anime_title)
        private val animeThumbnail: ImageView = itemView.findViewById(R.id.anime_thumbnail)
        private val animeIsDub: TextView = itemView.findViewById(R.id.is_dub_text)
        private val animeCardView: CardView = itemView.findViewById(R.id.anime_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_anime_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(animeList[position])
    }

    override fun getItemCount(): Int {
        return animeList.size - 1
    }
}