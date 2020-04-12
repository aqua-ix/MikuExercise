package com.aqua_ix.mikuexercise

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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

    enum class NumberOfTimes(val value: Int) {
        START(10),
        LITTLE(3)
    }

    companion object {
        private const val COUNT_DOWN_START_VALUE = 5000L

        private lateinit var sensorManager: SensorManager
        private lateinit var sensor: Sensor

        lateinit var position: Position
        var absRemaining = NumberOfTimes.START.value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContentView(R.layout.activity_exercise)
        endButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        countDownTimer.start()
        position = Position.DOWN
        absRemaining = NumberOfTimes.START.value

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
        playAudio(R.raw.voice_youi)
    }

    fun startAbs() {
        //audioUtil.playAudio(R.raw.voice_start)
        playAudio(R.raw.voice_start)
        counterText.text = "$absRemaining"
        messageText.text = this.getText(R.string.text_exercise_start)
        sensorManager.registerListener(accelerometer, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d(ExerciseActivity::class.toString(), "startAbs()")
    }

    private fun finishAbs() {
        //audioUtil.playAudio(R.raw.voice_otukaresamadesita)
        playAudio(R.raw.voice_otukaresamadesita)
        sensorManager.unregisterListener(accelerometer, sensor)
        messageText.text = this.getText(R.string.text_exercise_end)
        Log.d(ExerciseActivity::class.toString(), "finishAbs()")
    }

    fun countAbs() {
        absRemaining--
        counterText.text = ("$absRemaining")
        when (absRemaining) {
            NumberOfTimes.LITTLE.value -> {
                //audioUtil.playAudio(R.raw.voice_atosukosi)
                playAudio(R.raw.voice_atosukosi)
                messageText.text = this.getText(R.string.text_exercise_little)
                imageView.setImageDrawable(this.getDrawable(R.drawable.glad))
            }
            0 -> finishAbs()
        }
    }

    private fun playAudio(resource: Int){
        audioUtil.soundMap[resource]?.let {
            audioUtil.soundPool?.play(it, 1f, 1f, 1, 0, 1f)
        }
    }

    private val accelerometer = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                when {
                    position == Position.DOWN && event.values[0] > Acceleration.MAX.value -> {
                        position = Position.UP
                        countAbs()
                    }
                    position == Position.UP && event.values[0] < Acceleration.MIN.value -> {
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
            startAbs()
        }

        override fun onTick(millisUntilFinished: Long) {
            counterText.text = ("${countDown / 1000}")
            countDown -= 1000
        }
    }
}