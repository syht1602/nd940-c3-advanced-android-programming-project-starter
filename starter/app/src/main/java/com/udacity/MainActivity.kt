package com.udacity

import android.animation.ValueAnimator
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.notification.NOTIFICATION_ID
import com.udacity.notification.sendNotification


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0
    private var downloadType: DownloadType = DownloadType.NONE

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        requestNotificationPermission()
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                description = applicationContext.getString(R.string.notification_title)
                notificationManager.createNotificationChannel(this)
            }
        }

        // TODO: Implement code below
        with(binding.contentView.customButton) {
            setOnClickListener {
                this.updateButtonState(ButtonState.Loading)
                when (binding.contentView.radioGroup.checkedRadioButtonId) {
                    R.id.glideItem -> {
                        downloadType = DownloadType.GLIDE
                        binding.contentView.customButton.animateButton(ValueAnimator.INFINITE)
                        download()
                    }

                    R.id.loadAppItem -> {
                        downloadType = DownloadType.LOAD_APP
                        binding.contentView.customButton.animateButton(ValueAnimator.INFINITE)
                        download()
                    }

                    R.id.retrofitItem -> {
                        downloadType = DownloadType.RETROFIT
                        binding.contentView.customButton.animateButton(ValueAnimator.INFINITE)
                        download()
                    }

                    else -> {
                        binding.contentView.customButton.animateButton(0)
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.radio_not_selected),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val detailIntent = Intent(applicationContext, DetailActivity::class.java)
                detailIntent.putExtra(DOWNLOAD_TYPE, downloadType)
                val detailPendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_ID,
                    detailIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                notificationManager.sendNotification(
                    getString(R.string.notification_description),
                    context,
                    detailPendingIntent
                )
                binding.contentView.customButton.updateButtonState(ButtonState.Completed)
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Open the notification settings screen
            val intent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Notification permission is already granted.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 998
        const val CHANNEL_ID = "channelId"
        const val DOWNLOAD_TYPE = "DOWNLOAD_TYPE"
    }
}