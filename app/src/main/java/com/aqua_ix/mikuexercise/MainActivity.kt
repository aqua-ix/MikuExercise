package com.aqua_ix.mikuexercise

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var audioUtil: AudioUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        absButton.setOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)
        }

        audioUtil = AudioUtil.getInstance(this)
        audioUtil.createSoundMap(R.raw.voice_watasito)

    }

    override fun onStart() {
        super.onStart()
        audioUtil.playAudio(R.raw.voice_watasito)
    }
}
