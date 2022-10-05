package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    //    private lateinit var notificationManager: NotificationManager
//    private lateinit var pendingIntent: PendingIntent
//    private lateinit var action: NotificationCompat.Action
    private lateinit var binding: ActivityMainBinding
    private lateinit var urlData: UrlData
    //var downloadUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.content.customButton.setOnClickListener {
            val id = binding.content.radioGroup.checkedRadioButtonId
            //val c = binding.content.glideRadioButton.isChecked
            //Log.d("tagT","id $id id ${binding.content.glideRadioButton.id}")
            //https://stackoverflow.com/questions/4905075/how-to-check-if-url-is-valid-in-android
            urlData = getSelectedUrlData(id)
            if (urlData.url.isEmpty() || !URLUtil.isValidUrl(urlData.url)) Toast.makeText(
                applicationContext,
                getString(R.string.toastMessage),
                Toast.LENGTH_SHORT
            ).show()
            else {
                binding.content.customButton.buttonState = ButtonState.Loading
                download(urlData.url)
                //can set notification here but download manager show notification with download progress
//                sendNotification(
//                    urlData.fileName,
//                    "download in progress",
//                    getString(R.string.notification_title),
//                    "download in progress",
//                    R.string.notificationId,
//                    CHANNEL_ID,
//                    getString(R.string.downloadChannelName),
//                    getString(R.string.notification_button),
//                    applicationContext
//                )
            }

        }
    }

    //https://stackoverflow.com/questions/54786374/getting-downloadmanager-status-successful-even-after-download-not-successful
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            var downloadStatus = getString(R.string.downloadFail)

            if (id == downloadID) {
                val dmQuery = DownloadManager.Query()
                dmQuery.setFilterById(id)
                try {
                    downloadManager.query(dmQuery).use { cursor ->
                        if (cursor != null && cursor.count > 0) {
                            val columnIndex: Int =
                                cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            if (cursor.moveToFirst() && cursor.getInt(columnIndex) == DownloadManager.STATUS_SUCCESSFUL)
                                downloadStatus = getString(R.string.downloadSuccess)
//                             else {
//                                //set download fail but it is initialized as fail
////                                Log.e(TAG, "fail")
//                            }
                        }
                    }
                } catch (exception: Exception) {
                    //set download fail but it is initialized as fail or can show exception message
//                    Log.e(TAG, exception)
                }
//                val status = intent.getIntExtra( DownloadManager.COLUMN_STATUS, -1 )
//                val downloadStatus =
//                    if (status == DownloadManager.STATUS_SUCCESSFUL) getString(R.string.downloadSuccess)
//                    else getString(R.string.downloadFail)

                binding.content.customButton.buttonState = ButtonState.Completed
                //send notification
                sendNotification(
                    urlData.fileName,
                    downloadStatus,
                    getString(R.string.notification_title),
                    getString(R.string.notification_description),//can set download status here like fail or success
                    R.string.notificationId,
                    CHANNEL_ID,
                    getString(R.string.downloadChannelName),
                    getString(R.string.notification_button),
                    applicationContext
                )
//                Log.d("logT", "${urlData.fileName}  $status")

            }
            //Log.d("tagT","id $id,dId $downloadID")
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
    }

    private fun getSelectedUrlData(id: Int): UrlData {
        return when (id) {
            binding.content.glideRadioButton.id -> UrlData(
                GLIDE_URL,
                getString(R.string.radioGlide)
            )
            binding.content.retrofitRadioButton.id -> UrlData(
                RETROFIT_URL,
                getString(R.string.radioRetrofit)
            )
            binding.content.udacityRadioButton.id -> UrlData(
                UDACITY_URL,
                getString(R.string.radioUdacity)
            )
            binding.content.urlRadioButton.id -> UrlData(
                binding.content.editTextURL.text.toString(),
                getString(R.string.download_specific_url)
            )
            else -> UrlData()
        }
    }

    data class UrlData(var url: String = "", var fileName: String = "")

}
