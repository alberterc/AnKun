package com.ankun.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ankun.anime.AnimeEpisodeVideo
import com.ankun.R
import org.apache.commons.lang3.time.DateUtils
import java.sql.Date
import java.util.*

class AnimeEpisodesAdapter: RecyclerView.Adapter<AnimeEpisodesAdapter.ViewHolder> {
    private var context: Context?
    private var episodeList = emptyList<List<String>>()
    private var anime: String
    private var animeID: String

    constructor(context: Context, episodeList: List<List<String>>, anime: String, animeID: String) {
        this.context = context
        this.episodeList = episodeList
        this.anime = anime
        this.animeID = animeID
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(episode: List<String>, animeTitle: String, animeID: String) {
            // get anime episodes
            val episodeNum = "Episode " + episode[2]
            val episodeReleaseTime: String
            val episodeId = episode[1]

            // get episode release time
            val currDate = DateUtils.round(Date(System.currentTimeMillis()), Calendar.MINUTE)
            val releasedDate = DateUtils.round(Date(episode[3].toLong() * 1000), Calendar.MINUTE)
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

            // create card view for each episode item
            animeEpisodeNumber.text = episodeNum
            animeEpisodeReleaseTime.text = episodeReleaseTime

            // make each episode item clickable to watch them
            animeEpisodeCardView.setOnClickListener {
                val intent = Intent(it.context, AnimeEpisodeVideo::class.java)
                intent.putExtra("STREAM_URL", episodeId)
                intent.putExtra("ANIME_TITLE", animeTitle)
                intent.putExtra("ANIME_ID", animeID)
                it.context.startActivity(intent)
            }
        }

        private val animeEpisodeNumber: TextView = itemView.findViewById(R.id.anime_episode_num)
        private val animeEpisodeReleaseTime: TextView = itemView.findViewById(R.id.anime_episode_release_time)
        private val animeEpisodeCardView: CardView = itemView.findViewById(R.id.anime_episode_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_anime_episode, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(episodeList[position], anime, animeID)
    }

    override fun getItemCount(): Int {
        return episodeList.size
    }
}