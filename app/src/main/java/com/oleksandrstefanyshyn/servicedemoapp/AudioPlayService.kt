package com.oleksandrstefanyshyn.servicedemoapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import androidx.core.net.toUri
import java.io.File

class AudioPlayService : Service(), MediaPlayer.OnPreparedListener {

    private val mediaPlayer = mediaPlayerWithConfig {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        setOnPreparedListener(this@AudioPlayService)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return if (intent.action != null && intent.action.equals(STOP_SERVICE_ACTION)) {
            stopSelf()
            START_NOT_STICKY
        } else {
            if (mediaPlayer.isPlaying) {
                return START_NOT_STICKY
            }
            val audioUri = Uri.parse(intent.extras?.getString(AUDIO_URI))
            mediaPlayer.apply {
                setDataSource(applicationContext, audioUri)
                prepareAsync()
            }
            START_STICKY
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private inline fun mediaPlayerWithConfig(block: MediaPlayer.() -> Unit): MediaPlayer {
        return MediaPlayer().apply(block)
    }

    companion object {
        private const val STOP_SERVICE_ACTION = "STOP"
        private const val AUDIO_URI = "audioUri"

        fun play(context: Context, audioFile: File?) {
            val intent = Intent(context, AudioPlayService::class.java)
            intent.putExtra(AUDIO_URI, audioFile?.toUri().toString())
            context.startService(intent)
        }

        fun getStopIntent(context: Context): Intent {
            return Intent(context, AudioPlayService::class.java).apply {
                action = STOP_SERVICE_ACTION
            }
        }
    }
}
