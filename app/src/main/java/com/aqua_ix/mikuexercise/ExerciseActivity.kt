package com.aqua_ix.mikuexercise

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_exercise.*


class ExerciseActivity : AppCompatActivity() {

    lateinit var audioUtil: AudioUtil

    enum class Position {
        UP,
        DOWN
    }

    enum class Acceleration(val value: Int) {
        MAX(8),
        MIN(4)
    }

    companion object {
        private const val COUNT_DOWN_START_VALUE = 5000L
        private const val DEFAULT_TIMES_OF_ABS = 10

        private lateinit var sensorManager: SensorManager
        private lateinit var sensor: Sensor

        lateinit var position: Position
        var absRemaining = DEFAULT_TIMES_OF_ABS
        var absLittle = (absRemaining * 0.2).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = getOrientation()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContentView(R.layout.activity_exercise)
        endButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        position = Position.DOWN
        absRemaining = intent.getIntExtra("TIMES", DEFAULT_TIMES_OF_ABS)
        absLittle = (absRemaining * 0.2).toInt()

        audioUtil = AudioUtil.getInstance(this)
        audioUtil.createSoundMap(
            R.raw.voice_youi,
            R.raw.voice_start,
            R.raw.voice_atosukosi,
            R.raw.voice_otukaresamadesita
        )
        Log.d(ExerciseActivity::class.toString(), "onCreate()")
    }

    override fun onStart() {
        super.onStart()

        AlertDialog.Builder(this)
            .setMessage(getMessage())
            .setPositiveButton(R.string.dialog_start) { _, _ ->
                playAudio(R.raw.voice_youi)
                countDownTimer.start()
            }
            .show()

    }

    private fun getMessage(): Int {
        return when (intent.getStringExtra("MENU")) {
            getString(R.string.title_text_situp) -> R.string.dialog_text_situp
            getString(R.string.title_text_pushup) -> R.string.dialog_text_pushup
            getString(R.string.title_text_squat) -> R.string.dialog_text_squat
            else -> R.string.dialog_text_situp
        }
    }

    private fun getOrientation(): Int {
        return when (intent.getStringExtra("MENU")) {
            getString(R.string.title_text_situp) -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            getString(R.string.title_text_pushup) -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            getString(R.string.title_text_squat) -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun startExercise() {
        when (intent.getStringExtra("MENU")) {
            getString(R.string.title_text_situp) -> startAbs()
            getString(R.string.title_text_pushup) -> startAbs()
            getString(R.string.title_text_squat) -> startAbs()
            else -> startAbs()
        }
    }

    fun startAbs() {
        //audioUtil.playAudio(R.raw.voice_start)
        playAudio(R.raw.voice_start)
        counterText.setText(
            getString(R.string.exercise_text_times, absRemaining),
            TextView.BufferType.NORMAL
        )
        bubbleTextView.text = this.getText(R.string.exercise_text_remain)
        sensorManager.registerListener(accelerometer, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d(ExerciseActivity::class.toString(), "startAbs()")
    }

    private fun finishAbs() {
        //audioUtil.playAudio(R.raw.voice_otukaresamadesita)
        playAudio(R.raw.voice_otukaresamadesita)
        sensorManager.unregisterListener(accelerometer, sensor)
        bubbleTextView.text = this.getText(R.string.exercise_text_end)
        Log.d(ExerciseActivity::class.toString(), "finishAbs()")
    }

    fun countAbs() {
        absRemaining--
        counterText.setText(
            getString(R.string.exercise_text_times, absRemaining),
            TextView.BufferType.NORMAL
        )
        when (absRemaining) {
            absLittle -> {
                //audioUtil.playAudio(R.raw.voice_atosukosi)
                playAudio(R.raw.voice_atosukosi)
                bubbleTextView.text = this.getText(R.string.exercise_text_little)
                imageView.setImageDrawable(this.getDrawable(R.drawable.glad))
            }
            0 -> finishAbs()
        }
    }

    private fun playAudio(resource: Int) {
        audioUtil.soundMap[resource]?.let {
            audioUtil.soundPool?.play(it, 1f, 1f, 1, 0, 1f)
        }
    }

    private val accelerometer = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                when {
                    position == Position.DOWN && event.values[2] > Acceleration.MAX.value -> {
                        position = Position.UP
                        countAbs()
                    }
                    position == Position.UP && event.values[2] < Acceleration.MIN.value -> {
                        position = Position.DOWN
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private var countDownTimer = object : CountDownTimer(COUNT_DOWN_START_VALUE, 1000) {
        var countDown = COUNT_DOWN_START_VALUE

        override fun onFinish() {
            startExercise()
        }

        override fun onTick(millisUntilFinished: Long) {
            counterText.text = ("${countDown / 1000}")
            countDown -= 1000
        }
    }
}