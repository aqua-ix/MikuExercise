package com.aqua_ix.mikuexercise

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var audioUtil: AudioUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        absButton.setOnClickListener {
            showDialog(this)
        }

        audioUtil = AudioUtil.getInstance(this)
        audioUtil.createSoundMap(R.raw.voice_watasito)

    }

    override fun onStart() {
        super.onStart()
        audioUtil.playAudio(R.raw.voice_watasito)
    }

    private fun showDialog(context: Context) {
        class MainFragmentDialog : DialogFragment() {
            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                var builder: AlertDialog.Builder? = null
                activity?.let {
                    val inflater: LayoutInflater = it.layoutInflater
                    val view: View = inflater.inflate(R.layout.dialog, null, false)
                    val np = view.findViewById(R.id.numberPicker) as NumberPicker
                    np.minValue = 1
                    np.maxValue = 100
                    builder = AlertDialog.Builder(it)
                    builder?.setTitle(context.getString(R.string.title_text_pic_times))
                    builder?.setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        val intent = Intent(context, ExerciseActivity::class.java)
                            .putExtra("TIMES", np.value)
                        startActivity(intent)
                    }
                    builder?.setNegativeButton("Cancel", null)
                    builder?.setView(view)
                }
                return builder!!.create()
            }
        }
        // Dialogの表示
        val dialog = MainFragmentDialog()
        dialog.show(supportFragmentManager, "times_setting_dialog")
    }
}

