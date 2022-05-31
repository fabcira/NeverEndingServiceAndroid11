package it.torino.workmanagerlabclass


import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainCoroutineWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    companion object {
        private var TAG = this::class.simpleName
        private var NOTIFICATION_ID = 9973
    }

    override suspend fun doWork(): Result {
        // do not launch if the service is already alive
        if (CounterService.currentService == null) {
            withContext(Dispatchers.IO) {
                val trackerServiceIntent = Intent(context, CounterService::class.java)
                ServiceNotification.notificationText = "do not close the app, please"
                ServiceNotification.notificationIcon = R.drawable.ic_infinite_icon
                Log.i(TAG, "Launching tracker")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(trackerServiceIntent)
                } else {
                    context.startService(trackerServiceIntent)
                }
            }
        }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        ServiceNotification.notificationText = "do not close the app, please"
        ServiceNotification.notificationIcon = R.drawable.ic_infinite_icon
        val notification = ServiceNotification(context, NOTIFICATION_ID, true)
        return ForegroundInfo(NOTIFICATION_ID, notification.notification!!)
    }
}
