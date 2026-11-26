package com.openjarvis.local

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class DownloadService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("DownloadService", "DownloadService started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return null
    }
}
