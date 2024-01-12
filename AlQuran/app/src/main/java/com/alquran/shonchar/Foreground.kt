package com.alquran.shonchar

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat

class Foreground(val activity: Context) {

    private var iconNotification: Bitmap? = null
    private var notification: Notification? = null
    private var mNotificationManager: NotificationManager? = null

    @SuppressLint("LaunchActivityFromNotification")
    fun generateForegroundNotification(): Notification? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val broadCast = Intent(Constant.PAUSE)
            val pendingIntent = PendingIntent.getBroadcast(activity, 0, broadCast, PendingIntent.FLAG_IMMUTABLE)
            iconNotification =
                BitmapFactory.decodeResource(activity.resources, R.mipmap.ic_launcher_round)
            if (mNotificationManager == null) {
                mNotificationManager =
                    activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager != null)
                mNotificationManager?.createNotificationChannelGroup(
                    NotificationChannelGroup("chats_group", "Chats")
                )
                val notificationChannel = NotificationChannel(
                    "service_channel", "Service Notifications", NotificationManager.IMPORTANCE_MIN
                )
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }
            val builder = NotificationCompat.Builder(activity, "service_channel")

            builder.setContentTitle(
                StringBuilder(activity.resources.getString(R.string.app_name)).append(
                    " is playing"
                ).toString()
            ).setTicker(
                StringBuilder(activity.resources.getString(R.string.app_name)).append("service is running")
                    .toString()
            ).setContentText("Touch to stop").setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_LOW).setWhen(0).setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent).setOngoing(true)

            if (iconNotification != null) {
                builder.setLargeIcon(
                    Bitmap.createScaledBitmap(
                        iconNotification!!, 128, 128, false
                    )
                )
            }

            builder.color = activity.resources.getColor(R.color.blue)
            notification = builder.build()
        }
        return notification
    }
}