package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


private const val REQUEST_CODE = 0
const val URL_FILE_NAME_KEY = "urlFileName"
const val URL_STATUS_KEY = "urlStatus"

fun sendNotification(
    fileName: String,
    downloadStatus: String,
    title: String,
    messageBody: String,
    notificationId: Int,
    channelId: String,
    channelName: String,
    actionButtonString: String,
    context: Context
) {

    val notificationManager =
        ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

    //create channel
    notificationManager.createChannel(
        channelId,
        channelName,
        context.getString(R.string.notification_description)
    )

    notificationManager.sendNotification(
        fileName,
        downloadStatus,
        title,
        messageBody,
        notificationId,
        channelId,
        actionButtonString,
        context
    )
}

private fun NotificationManager.sendNotification(
    fileName: String,
    downloadStatus: String,
    title: String,
    messageBody: String,
    notificationId: Int,
    channelId: String,
    actionButtonString: String,
    context: Context
) {

    val contentIntent = Intent(context, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    // feedback action
//    val actionIntent = Intent(context, DetailsReceiver::class.java).apply {
//        putExtra(URL_FILE_NAME_KEY, fileName)
//        putExtra(URL_STATUS_KEY, downloadStatus)
//    }
//
//    val actionPendingIntent: PendingIntent = PendingIntent.getBroadcast(
//        context,
//        REQUEST_CODE,
//        actionIntent,
//        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//    )

// https://stackoverflow.com/questions/41238380/how-to-prevent-notification-bar-from-closing-upon-notification-action-click
//remove broadcast and use getActivity with the action to dismiss notification drawer when click action button
    val actionIntent = Intent(context, DetailActivity::class.java).apply {
        putExtra(URL_FILE_NAME_KEY, fileName)
        putExtra(URL_STATUS_KEY, downloadStatus)
    }

    val actionPendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        actionIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(context, channelId)

        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        //action
//        .addAction(
//            NotificationCompat.Action(
//                null,
//                actionButtonString,
//                actionPendingIntent
//            )
//        )
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            actionButtonString,
            actionPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notify(notificationId, builder.build())
}

private fun NotificationManager.createChannel(
    channelId: String,
    channelName: String,
    channelDescription: String
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )//disable badges for this channel
            .apply {
                setShowBadge(false)
            }

        notificationChannel.description = channelDescription

        createNotificationChannel(notificationChannel)

    }
}

//class DetailsReceiver : BroadcastReceiver() {
//    override fun onReceive(p0: Context?, p1: Intent?) {
//        val fileName = p1?.getStringExtra(URL_FILE_NAME_KEY)
//        val downloadStatus = p1?.getStringExtra(URL_STATUS_KEY)
//        val intent = Intent(p0, DetailActivity::class.java).apply {
//            putExtra(URL_FILE_NAME_KEY, fileName)
//            putExtra(URL_STATUS_KEY, downloadStatus)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        }
//        p0?.cancelNotifications()
//        p0?.startActivity(intent)
//        //Log.d("logT2","$fileName  $downloadStatus")
//    }
//
//}

fun Context.cancelNotifications() {
    val notificationManager =
        ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    notificationManager.cancelNotifications()
}

private fun NotificationManager.cancelNotifications() {
    cancelAll()
}




