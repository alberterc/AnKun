package com.ankun

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.Util
import com.google.android.material.appbar.MaterialToolbar


class AnimeEpisodeVideo : AppCompatActivity() {
    private val animeScrape = AnimeScrape(this)

    private var playWhenReady = true
    private var playbackPosition = 0L

    private lateinit var vidPlayerView: StyledPlayerView
    private lateinit var vidPlayerContainer: AspectRatioFrameLayout
    private lateinit var topToolbarView: MaterialToolbar
    private lateinit var animeTitleView: TextView
    private lateinit var animeEpisodesRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_episode_video)

        vidPlayerView = findViewById(R.id.vid_player_view)
        vidPlayerContainer = findViewById(R.id.vid_player_container)
        topToolbarView = findViewById(R.id.top_toolbar)
        animeTitleView = findViewById(R.id.anime_title)
        animeEpisodesRv = findViewById(R.id.episode_list)

        animeScrape
            .GetEpisodeVideoStream(intent.extras!!.getString("STREAM_URL")!!, intent.extras!!.getString("ANIME_TITLE")!!, intent.extras!!.getString("ANIME_ID")!!)
            .execute()

        // set default aspect ratio for video player container
        vidPlayerContainer.setAspectRatio(16f/9f)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val params = vidPlayerContainer.layoutParams

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi()
            animeTitleView.visibility = View.GONE
            topToolbarView.visibility = View.GONE
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            vidPlayerContainer.layoutParams = params
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            showSystemUi()
            animeTitleView.visibility = View.VISIBLE
            topToolbarView.visibility = View.VISIBLE
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            vidPlayerContainer.layoutParams = params
        }
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }
    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun hideSystemUi() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
    private fun showSystemUi() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Show both the status bar and the navigation bar
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }
    private fun releasePlayer() {
        animeScrape.vidPlayer.let {
            playWhenReady = it!!.playWhenReady
            playbackPosition = it.currentPosition
            it.release()
        }
    }
}