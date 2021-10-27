package com.augustin26.studyingistiming.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.augustin26.studyingistiming.R
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_youtube.*


class YoutubeActivity : YouTubeBaseActivity() {

    companion object {
        val VIDEO_ID: String = "5UDKmg3Gl0M";
        val YOUTUBE_API_KEY: String = "AIzaSyBOHXyP_hrmEHTthvGs5A5cAg2TrFGMlMs"
    }

    lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI() {
        youtubePlayerInit = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, youtubePlayer: YouTubePlayer?, p2: Boolean) {
                youtubePlayer?.loadVideo(VIDEO_ID)
            }

            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                Toast.makeText(applicationContext, "Something went wrong !! ", Toast.LENGTH_SHORT).show()
            }
        }

        btnPlay.setOnClickListener {
            youtubePlayer.initialize(YOUTUBE_API_KEY, youtubePlayerInit)
        }
    }

}