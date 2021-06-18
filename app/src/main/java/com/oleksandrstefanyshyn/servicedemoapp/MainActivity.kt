package com.oleksandrstefanyshyn.servicedemoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.oleksandrstefanyshyn.servicedemoapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var audioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.download.setOnClickListener {
            Toast.makeText(this, R.string.downloading_started, Toast.LENGTH_SHORT).show()
            audioFile = File(cacheDir, AUDIO_FILE_NAME)
            viewModel.download(URL, audioFile)
        }
        binding.play.setOnClickListener {
            if (audioFile == null) {
                Toast.makeText(this, R.string.please_download, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.playing_started, Toast.LENGTH_SHORT).show()
                AudioPlayService.play(this, audioFile)
                createNotification()
            }
        }
        viewModel.downloadState.observe(this, {
            when (it) {
                is MainViewModel.DownloadState.Error -> {
                    val message = it.reasonString ?: getString(it.reasonRes)
                    Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MainViewModel.DownloadState.Success -> Toast.makeText(
                    this,
                    R.string.successfully_downloaded,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() {
        val stopServiceIntent = AudioPlayService.getStopIntent(this)
        val pendingIntent =
            PendingIntent.getService(
                application,
                0,
                stopServiceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

        val builder = NotificationCompat.Builder(application, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.playing))
            .setContentText(getString(R.string.tap_stop))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(application)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        private const val AUDIO_FILE_NAME = "audio.mp3"
        const val URL =
            "https://www.silvermansound.com/music/direct_download.php?file=dirty-gertie.mp3"
        const val CHANNEL_ID = "CHANNEL_ID"
        const val NOTIFICATION_ID = 123
    }
}
