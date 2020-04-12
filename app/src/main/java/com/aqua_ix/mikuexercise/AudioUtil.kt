package com.aqua_ix.mikuexercise

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class AudioUtil(private val context: Context) {

    val soundMap = mutableMapOf<Int, Int>()
    var soundPool: SoundPool? = null

    companion object {
        private var instance: AudioUtil? = null
        @JvmStatic
        fun getInstance(context: Context): AudioUtil =
            instance ?: synchronized(this) {
                instance ?: AudioUtil(context).also {
                    instance = it
                }

            }
    }

    private fun getAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
    }

    private fun getSoundPool(maxStream: Int): SoundPool {
        return SoundPool.Builder()
            .setAudioAttributes(getAudioAttributes())
            .setMaxStreams(maxStream)
            .build()
    }

    fun createSoundMap(vararg resources: Int){
        soundPool = getSoundPool(resources.size)
        for (resource in resources) {
            soundPool?.let {
                soundMap[resource] = it.load(context, resource, 1)
            }
        }
    }

    fun playAudio(resource: Int) {
        soundPool?.setOnLoadCompleteListener { soundPool, _, status ->
            if(status == 0) {
                soundMap[resource]?.let {
                    soundPool?.play(it, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

}

