package com.aqua_ix.mikuexercise

import android.content.Context
import android.content.DialogInterface
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
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_exercise.*


class ExerciseActivity : AppCompatActivity(), DialogInterface.OnClickListener {

    lateinit var audioUtil: AudioUtil

    enum class Position {
        UP,
        DOWN
    }

    enum class Acceleration(val value: Int) {
        SITUP_MAX(10),
        SITUP_MIN(-10),
        SQUAT_MAX(20),
        SQUAT_MIN(5)
    }

    companion object {
        private const val COUNT_DOWN_START_VALUE = 5000L
        private const val DEFAULT_TIMES_OF_SITUP = 10

        private lateinit var sensorManager: SensorManager
        private lateinit var sensor: Sensor

        lateinit var position: Position
        var exerciseRemaining = DEFAULT_TIMES_OF_SITUP
        var exerciseLittle = (exerciseRemaining * 0.2).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = getOrientation()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(getSensorMode())

        setContentView(R.layout.activity_exercise)
        endButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            reset()
        }

        position = Position.DOWN
        exerciseRemaining = intent.getIntExtra("TIMES", DEFAULT_TIMES_OF_SITUP)
        exerciseLittle = (exerciseRemaining * 0.2).toInt()

        audioUtil = AudioUtil.getInstance(this)
        audioUtil.createSoundMap(
            R.raw.voice_youi,
            R.raw.voice_start,
            R.raw.voice_atosukosi,
            R.raw.voice_otukaresamadesita
        )

        val dialog = StartDialogFragment()
        val args = Bundle()
        args.putInt("msg", getMessage())
        dialog.arguments = args
        dialog.show(supportFragmentManager, "startDialog")
        if (savedInstanceState != null) {
            dialog.dismiss()
        }
        Log.d(ExerciseActivity::class.toString(), "onCreate()")
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> countDownTimer.start()
        }
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

    private fun getSensorMode(): Int {
        return when (intent.getStringExtra("MENU")) {
            getString(R.string.title_text_situp) -> Sensor.TYPE_ACCELEROMETER
            getString(R.string.title_text_pushup) -> Sensor.TYPE_PROXIMITY
            getString(R.string.title_text_squat) -> Sensor.TYPE_ACCELEROMETER
            else -> Sensor.TYPE_ACCELEROMETER
        }
    }

    private fun startExercise() {
        //audioUtil.playAudio(R.raw.voice_start)
        playAudio(R.raw.voice_start)
        counterText.setText(
            getString(R.string.exercise_text_times, exerciseRemaining),
            TextView.BufferType.NORMAL
        )
        bubbleTextView.text = this.getText(R.string.exercise_text_remain)
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d(ExerciseActivity::class.toString(), "startExercise()")
    }

    private fun finishExercise() {
        //audioUtil.playAudio(R.raw.voice_otukaresamadesita)
        playAudio(R.raw.voice_otukaresamadesita)
        sensorManager.unregisterListener(sensorEventListener, sensor)
        bubbleTextView.text = this.getText(R.string.exercise_text_end)
        Log.d(ExerciseActivity::class.toString(), "finishExercise()")
    }

    private fun reset() {
        countDownTimer.cancel()
        sensorManager.unregisterListener(sensorEventListener, sensor)
        finish()
    }

    private fun countExercise() {
        exerciseRemaining--
        counterText.setText(
            getString(R.string.exercise_text_times, exerciseRemaining),
            TextView.BufferType.NORMAL
        )
        when (exerciseRemaining) {
            exerciseLittle -> {
                //audioUtil.playAudio(R.raw.voice_atosukosi)
                playAudio(R.raw.voice_atosukosi)
                bubbleTextView.text = this.getText(R.string.exercise_text_little)
                imageView.setImageDrawable(this.getDrawable(R.drawable.glad))
            }
            0 -> finishExercise()
        }
    }

    private fun playAudio(resource: Int) {
        audioUtil.soundMap[resource]?.let {
            audioUtil.soundPool?.play(it, 1f, 1f, 1, 0, 1f)
        }
    }

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {

            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                when {
                    position == Position.DOWN && getSensorValue(event) > getAccMax() -> {
                        position = Position.UP
                        countExercise()
                    }
                    position == Position.UP && getSensorValue(event) < getAccMin() -> {
                        position = Position.DOWN
                    }
                }
            } else if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
                if(event.values[0] < sensor.maximumRange) {
                    Log.d("sensor", event.values[0].toString())
                    countExercise()
                }
            }
        }

        private fun getSensorValue(event: SensorEvent): Float {
            return when (intent.getStringExtra("MENU")) {
                getString(R.string.title_text_situp) -> event.values[2]
                getString(R.string.title_text_squat) -> event.values[0]
                else -> 0f
            }
        }

        private fun getAccMax(): Int{
            return when (intent.getStringExtra("MENU")) {
                getString(R.string.title_text_situp) -> Acceleration.SITUP_MAX.value
                getString(R.string.title_text_squat) -> Acceleration.SQUAT_MAX.value
                else -> 0
            }
        }

        private fun getAccMin():Int{
            return when (intent.getStringExtra("MENU")) {
                getString(R.string.title_text_situp) -> Acceleration.SITUP_MIN.value
                getString(R.string.title_text_squat) -> Acceleration.SQUAT_MIN.value
                else -> 0
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