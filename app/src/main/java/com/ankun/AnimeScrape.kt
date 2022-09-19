package com.ankun

import android.app.Activity
import android.os.AsyncTask
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ankun.adapter.AnimeEpisodesAdapter
import com.ankun.adapter.AnimeGenreAdapter
import com.ankun.adapter.AnimeLatestReleaseAdapter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import java.lang.ref.WeakReference
import java.net.URL


@Suppress("DEPRECATION")
class AnimeScrape(context: Activity) {
    private var baseUrl = "https://animension.to/"
    private var activityReference = WeakReference(context)
    private var animeEpisodesList: List<List<String>> = mutableListOf()
    lateinit var vidPlayer: ExoPlayer

    inner class GetLatestList : AsyncTask<Void, Void, Void>() {
        private var latestListPage = "1" // anime list page starts from 1
        private var latestListMode = "sub" // anime type: "sub" or "dub"
        private var urlLatestListData = baseUrl + "public-api/index.php?page=${latestListPage}&mode=${latestListMode}"
        private var animeLatestList: List<List<String>> = mutableListOf()

        override fun doInBackground(vararg params: Void?): Void? {
            // get anime latest list data from JSON response
            // retrieves JSON response in String data type
            val animeLatestListJSONStr = URL(urlLatestListData).readText()
                .replace("\\", "")
                .replace("[", "")
                .replace("]", "")
            // convert from String into List of List
            // [[Anime Title, Anime ID, UNKNOWN, TOTAL EP, ANIME THUMBNAIL, LAST EP RELEASE TIME]]
            animeLatestList = animeLatestListJSONStr
                .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
                .chunked(6)

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            val animeLatestListRv: RecyclerView = activityReference.get()!!.findViewById(R.id.anime_list)
            animeLatestListRv.adapter = AnimeLatestReleaseAdapter(activityReference.get()!!.applicationContext, animeLatestList)
        }

    }

    inner class GetAnimeDetails(animeID: String) : AsyncTask<Void, Void, Void>() {
        private var urlAnimeDetails = baseUrl + animeID
        private var urlAnimeEpisodes = baseUrl + "public-api/episodes.php?id=${animeID}"
        private var animeDetailsMap: MutableMap<String, Any> = mutableMapOf()
        private val mAnimeID = animeID

        override fun doInBackground(vararg params: Void?): Void? {
            // get anime details data from HTML
            val webPage = Jsoup.connect(urlAnimeDetails).get()
            val animeAllDetailsHTML = webPage.select("div.infox")
            val animeDetails = animeAllDetailsHTML.select("div.spe")
            val animeGenreList: MutableList<Map<String, String>> = mutableListOf()

            val animeTitle = animeAllDetailsHTML.select("h1.entry-title")[0].text().trim()
            animeDetailsMap["animeTitle"] = animeTitle

            val animeDescription = animeAllDetailsHTML.select("div.desc")[0].text().trim()
            animeDetailsMap["animeDescription"] = animeDescription

            val animeThumbnail = webPage.select("div.thumbook").select("img")[0].absUrl("src")
            animeDetailsMap["animeThumbnail"] = animeThumbnail

            val animeGenre = animeAllDetailsHTML.select("div.genxed").select("span").select("a")
            for (genre in animeGenre) {
                val eachGenreMap: Map<String, String> = mapOf(
                    "genreType" to genre.text().trim(),
                    "genreURL" to genre.absUrl("href")
                )
                animeGenreList.add(eachGenreMap)
            }
            animeDetailsMap["animeGenres"] = animeGenreList

            for (detail in animeDetails.select("span")) {
                val animeDetail = detail.text()
                if (animeDetail.substringBefore(":").trim() == "Status") {
                    animeDetailsMap["animeStatus"] = animeDetail.substringAfter(":").trim()
                }
                else if (animeDetail.substringBefore(":").trim() == "Type") {
                    animeDetailsMap["animeType"] = animeDetail.substringAfter(":").trim()
                }
                else if (animeDetail.substringBefore(":").trim() == "Season") {
                    animeDetailsMap["animeSeason"] = animeDetail.substringAfter(":").trim()
                }
                else if (animeDetail.substringBefore(":").trim() == "Episodes") {
                    animeDetailsMap["animeEpisodes"] = animeDetail.substringAfter(":").trim()
                }
            }

            // get anime episodes data from JSON response
            // retrieves JSON response in String data type
            val animeEpisodesJSONStr = URL(urlAnimeEpisodes).readText()
                .replace("\\", "")
                .replace("[", "")
                .replace("]", "")
            // convert from String into List of List
            // [[UNKNOWN, EPISODE ID, EPISODE NUMBER, EPISODE RELEASE TIME]]
            animeEpisodesList = animeEpisodesJSONStr
                .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
                .chunked(4)

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            val animeTitle: TextView = activityReference.get()!!.findViewById(R.id.anime_title)
            val animeDescription: TextView = activityReference.get()!!.findViewById(R.id.anime_description_text)
            val animeThumbnail: ImageView = activityReference.get()!!.findViewById(R.id.anime_thumbnail)
            val animeStatus: TextView = activityReference.get()!!.findViewById(R.id.anime_state_text)
            val animeEpisodes: TextView = activityReference.get()!!.findViewById(R.id.episode_num_text)
            val animeType: TextView = activityReference.get()!!.findViewById(R.id.anime_type_text)
            val animeSeason: TextView = activityReference.get()!!.findViewById(R.id.anime_season_text)
            val episodesRv: RecyclerView = activityReference.get()!!.findViewById(R.id.episode_list)
            val genreRv: RecyclerView = activityReference.get()!!.findViewById(R.id.genre_list)

            animeTitle.text = animeDetailsMap["animeTitle"].toString()
            animeDescription.text = animeDetailsMap["animeDescription"].toString()
            animeStatus.text = animeDetailsMap["animeStatus"].toString()
            animeEpisodes.text = animeDetailsMap["animeEpisodes"].toString()
            animeType.text = animeDetailsMap["animeType"].toString()
            animeSeason.text = animeDetailsMap["animeSeason"].toString()
            Picasso.get()
                .load(animeDetailsMap["animeThumbnail"].toString())
                .into(animeThumbnail)

            episodesRv.adapter =
                AnimeEpisodesAdapter(activityReference.get()!!.applicationContext, animeEpisodesList, animeDetailsMap["animeTitle"].toString(), mAnimeID)
            genreRv.adapter = AnimeGenreAdapter(activityReference.get()!!.applicationContext,
                animeDetailsMap["animeGenres"] as List<Map<String, String>>
            )
        }

    }

    inner class GetEpisodeVideoStream(episodeID: String, anime: String, animeID: String) : AsyncTask<Void, Void, Void>() {
        private var urlEpisodeLinks = baseUrl + "public-api/episode.php?id=${episodeID}"
        private var urlAnimeEpisodes = baseUrl + "public-api/episodes.php?id=${animeID}"
        private var episodeDetailsList: MutableList<String> = mutableListOf()
        private var episodeDetailsMap: MutableMap<String, String> = mutableMapOf()
        private val animeTitle = anime
        private val mAnimeID = animeID

        override fun doInBackground(vararg params: Void?): Void? {
            // get anime episodes data from JSON response
            // retrieves JSON response in String data type
            val animeEpisodesJSONStr = URL(urlAnimeEpisodes).readText()
                .replace("\\", "")
                .replace("[", "")
                .replace("]", "")
            // convert from String into List of List
            // [[UNKNOWN, EPISODE ID, EPISODE NUMBER, EPISODE RELEASE TIME]]
            animeEpisodesList = animeEpisodesJSONStr
                .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
                .chunked(4)

            // get anime episode stream url
            // using "Direct server" or "v.vrv.co"
            val episodeDetailsJSON = URL(urlEpisodeLinks).readText()
                .replace("\\", "")
                .replace("[", "")
                .replace("]", "")
                .replace("\"{", "")
                .replace("}\"", "")

            // convert from String into List
            // [UNKNOWN, UNKNOWN, OFFICIAL SOURCES.., UNOFFICIAL SOURCES..]
            episodeDetailsList = episodeDetailsJSON
                .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex()) as MutableList<String>
            // remove the first 2 index from list
            episodeDetailsList.removeAt(0)
            episodeDetailsList.removeAt(0)
            // remove OFFICIAL SOURCES index if null
            if (episodeDetailsList[0] == "null") {
                episodeDetailsList.removeAt(0)
            }
            // remove last index (episode num)
            val episodeNum = episodeDetailsList[episodeDetailsList.lastIndex]
            episodeDetailsList.removeAt(episodeDetailsList.lastIndex)

            // convert from List to Map
            episodeDetailsMap = episodeDetailsList
                .associate {
                    val (key, value) = it.split("\":\"")
                    key.replace("\"", "") to value.replace("\"", "")
                } as MutableMap<String, String>
            episodeDetailsMap["episodeNum"] = episodeNum

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            val ctxRef = activityReference.get()!!
            val urlStream = episodeDetailsMap["Direct-directhls"]!!

            val topToolbarView: MaterialToolbar = ctxRef.findViewById(R.id.top_toolbar)
            val animeTitleView: TextView = ctxRef.findViewById(R.id.anime_title)
            val vidPlayerView: StyledPlayerView = ctxRef.findViewById(R.id.vid_player_view)
            val episodeRecyclerView: RecyclerView = ctxRef.findViewById(R.id.episode_list)

            // change top toolbar title to what episode is selected
            topToolbarView.title = "Episode ${episodeDetailsMap["episodeNum"]}"
            // change anime title to what is selected
            animeTitleView.text = animeTitle


            ///// START OF VIDEO PLAYER (EXOPLAYER) INITIALIZATION /////
            // create track selector
            // sets max video resolution to standard definition (SD) to safe data expense
            val trackSelector = DefaultTrackSelector(ctxRef.applicationContext)
                .apply {
                    setParameters(buildUponParameters().setMaxVideoSizeSd())
                }

            // create exoplayer instance
            vidPlayer = ExoPlayer.Builder(ctxRef.applicationContext)
                .setTrackSelector(trackSelector)
                .setSeekBackIncrementMs(10000)
                .setSeekForwardIncrementMs(10000)
                .build()
                .also {
                    vidPlayerView.player = it
                    it.setMediaItem(MediaItem.fromUri(urlStream))
                    it.prepare()
                }
            ///// END OF VIDEO PLAYER (EXOPLAYER) INITIALIZATION /////

            // initialize episode list
            episodeRecyclerView.layoutManager = GridLayoutManager(ctxRef.applicationContext, 2)
            episodeRecyclerView.adapter =
                AnimeEpisodesAdapter(ctxRef.applicationContext, animeEpisodesList, animeTitle, mAnimeID)
        }
    }
}