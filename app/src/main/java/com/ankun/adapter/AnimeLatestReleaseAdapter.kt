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
import com.ankun.anime.AnimeDetails
import com.ankun.R
import com.squareup.picasso.Picasso
import org.apache.commons.lang3.time.DateUtils
import java.sql.Date
import java.util.*

class AnimeLatestReleaseAdapter: RecyclerView.Adapter<AnimeLatestReleaseAdapter.ViewHolder> {
    private var context: Context?
    private var animeList = emptyList<List<String>>()
    private var mode : String

    constructor(context: Context, animeList: List<List<String>>, mode: String) {
        this.context = context
        this.animeList = animeList
        this.mode = mode
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(anime: List<String>, animeMode: String) {
            // get each anime details from anime list
            val title = anime[0].replace("\"", "")
            val thumbnail = anime[4].replace("\"", "")
            val episodeNum = "Ep " + anime[3]
            val episodeReleaseTime: String

            // get anime episode release time
            val currDate = DateUtils.round(Date(System.currentTimeMillis()), Calendar.MINUTE)
            val releasedDate = DateUtils.round(Date(anime[5].toLong() * 1000), Calendar.MINUTE)
            val timeInterval: Long = currDate.time - releasedDate.time
            val seconds = timeInterval / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            // simple string formatting for episode release time
            if (days > 1) {
                episodeReleaseTime = "$days days ago"
            }
            else if (days > 0) {
                episodeReleaseTime = "$days day ago"
            }
            else if (hours > 1) {
                episodeReleaseTime = "$hours hrs. ago"
            }
            else if (hours > 0) {
                episodeReleaseTime = "$hours hr. ago"
            }
            else if (minutes > 1) {
                episodeReleaseTime = "$minutes mins. ago"
            }
            else if (minutes > 0) {
                episodeReleaseTime = "$minutes min. ago"
            }
            else if (seconds > 1) {
                episodeReleaseTime = "$seconds secs. ago"
            }
            else if (seconds > 0) {
                episodeReleaseTime = "$seconds sec. ago"
            }
            else {
                episodeReleaseTime = "Just Released"
            }

            // create card view for each anime item
            animeTitle.text = title
            animeEpisodeNum.text = episodeNum
            animeEpisodeReleaseTime.text = episodeReleaseTime
            Picasso.get()
                .load(thumbnail)
                .into(animeThumbnail)
            if (animeMode == "dub") {
                animeSubText.visibility = View.VISIBLE
            }
            else {
                animeSubText.visibility = View.GONE
            }

            // make each anime item clickable to show anime details
            animeCardView.setOnClickListener {
                val intent = Intent(it.context, AnimeDetails::class.java)
                intent.putExtra("anime_id", anime[1])
                it.context.startActivity(intent)
            }
        }

        private val animeTitle: TextView = itemView.findViewById(R.id.anime_title)
        private val animeThumbnail: ImageView = itemView.findViewById(R.id.anime_thumbnail)
        private val animeEpisodeNum: TextView = itemView.findViewById(R.id.anime_episode_num)
        private val animeEpisodeReleaseTime: TextView = itemView.findViewById(R.id.anime_episode_release_time)
        private val animeSubText: TextView = itemView.findViewById(R.id.is_dub_text)
        private val animeCardView: CardView = itemView.findViewById(R.id.anime_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_anime_detailed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(animeList[position], mode)
    }

    override fun getItemCount(): Int {
        return animeList.size - 1
    }
}