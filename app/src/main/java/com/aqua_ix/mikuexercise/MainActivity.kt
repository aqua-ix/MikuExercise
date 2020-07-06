package com.aqua_ix.mikuexercise

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var audioUtil: AudioUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val items = arrayOf(
            getString(R.string.title_text_situp),
            getString(R.string.title_text_pushup),
            getString(R.string.title_text_squat)
        )
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            items
        )

        title_menu_dropdown.setAdapter(adapter)


        startButton.setOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java).putExtra(
                "TIMES",
                Integer.parseInt(title_times_input.text.toString())
            )
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

