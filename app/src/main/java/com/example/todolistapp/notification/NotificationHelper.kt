package com.example.todolistapp.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.todolistapp.FirstScreenActivity
import com.example.todolistapp.R

const val CHANNEL_ID = "testID"
const val NOTIFICATION_ID = "1456"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class NotificationHelper: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val resultIntent = Intent(context, FirstScreenActivity::class.java)
        val resultPendingIntent = TaskStackBuilder.create(context).run{
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        Log.d("MyTag", "onReceive: PASSSSSSSED")
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID.toInt(), notification)
    }
}