package com.aqua_ix.mikuexercise

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var audioUtil: AudioUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            with(savedInstanceState) {
                // Restore value of members from saved state
                title_menu_selection.setText(getString(MENU_SELECTION), TextView.BufferType.NORMAL)
                title_times_input.setText(getString(TIMES_INPUT), TextView.BufferType.NORMAL)
            }
        } else {
            title_menu_selection.setText(
                getString(R.string.title_text_situp),
                TextView.BufferType.NORMAL
            )
        }

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
        // TODO 回転時調整
        title_menu_selection.setAdapter(adapter)

        startButton.setOnClickListener {

            if (title_times_input.text.toString().isEmpty()) {
                timesInputLayout.error = getString(R.string.title_error_times)
                return@setOnClickListener
            } else {
                timesInputLayout.error = null
            }

            val intent = Intent(this, ExerciseActivity::class.java).putExtra(
                "TIMES",
                Integer.parseInt(title_times_input.text.toString())
            ).putExtra("MENU", title_menu_selection.text.toString())
            startActivity(intent)
        }

        audioUtil = AudioUtil.getInstance(this)
        audioUtil.createSoundMap(R.raw.voice_watasito)
    }

    override fun onStart() {
        super.onStart()
        audioUtil.playAudio(R.raw.voice_watasito)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the user's current game state
        outState.run {
            putString(MENU_SELECTION, title_menu_selection.text.toString())
            putString(TIMES_INPUT, title_times_input.text.toString())
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val MENU_SELECTION = "MenuSelection"
        const val TIMES_INPUT = "TimesInput"
    }
}

