package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ContentMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
  private lateinit var binding: ContentMainBinding

    private lateinit var contentIntent: Intent
    private var downloadID: Long = 0
    private lateinit var downloadManager: DownloadManager

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        notificationManager = ContextCompat.getSystemService(
            this, NotificationManager::class.java
        ) as NotificationManager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        contentIntent = Intent(applicationContext, DetailActivity::class.java)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(
            getString(R.string.download_channel_id),
            getString(R.string.download_channel_name)
        )

        download_button.setOnClickListener {

            when (binding.radioGroupDownload.checkedRadioButtonId) {
                R.id.rb_glide -> {
                    download(Const.URL_GLIDE)
                    contentIntent.putExtra(
                        Const.INTENT_FILENAME,
                        getString(R.string.radio_text_glide)
                    )
                }
                R.id.rb_retrofit -> {
                    download(Const.URL_RETROFIT)
                    contentIntent.putExtra(
                        Const.INTENT_FILENAME,
                        getString(R.string.radio_text_retrofit)
                    )
                }
                R.id.rb_udacity -> {
                    download(Const.URL_UDACITY)
                    contentIntent.putExtra(
                        Const.INTENT_FILENAME,
                        getString(R.string.radio_text_udacity)
                    )
                }
                else -> {
                    Toast.makeText(this, "no file selected to download", Toast.LENGTH_SHORT).show()

                }
            }
        }


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                Toast.makeText(
                    context,
                    getString(R.string.notification_description),
                    Toast.LENGTH_SHORT
                ).show()
                binding.downloadButton.setState(ButtonState.Completed)
            }

            val cursor: Cursor =
                downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            while (cursor.moveToNext()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {

                    DownloadManager.STATUS_RUNNING -> {
                        val total =
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        if (total >= 0) {

                            val downloaded =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            // I think this is progress
                            val progress = downloaded / total * 100

                        }
                    }
                    DownloadManager.STATUS_FAILED -> {
                        contentIntent.putExtra(Const.INTENT_STATUS, getString(R.string.status_fail))

                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        contentIntent.putExtra(
                            Const.INTENT_STATUS,
                            getString(R.string.status_success)
                        )
                    }
                }

            }


            notificationManager.sendNotification(
                getString(R.string.notification_description),
                this@MainActivity,
                contentIntent
            )


        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download some stuff "

            notificationManager.createNotificationChannel(notificationChannel)

        }
    }


    private fun download(url: String) {

        download_button.setState(ButtonState.Loading)
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        downloadID = downloadManager.enqueue(request)


    }


}
