/*
 * Copyright (c) Code Developed by Prof. Fabio Ciravegna
 * All rights Reserved
 */
package it.torino.workmanagerlabclass

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.*
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class CounterService : Service() {
    private var wakeLock: WakeLock? = null
    private var currentServiceNotification: ServiceNotification? = null
    private lateinit var repeatJob :Job
    private lateinit var wakeLockJob :Job

    companion object {
        var currentService: CounterService? = null

        // it is static so to make sure that it is always initialised when the viewmodel live data is
        // is created, otherwise you risk a disconnection
        var counter: MutableLiveData<Int> = MutableLiveData(0)
        private val TAG = CounterService::class.java.simpleName
        private const val NOTIFICATION_ID = 9974
    }

    override fun onCreate() {
        super.onCreate()
        currentService = this
    }


    /**
     * it starts the foreground process
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "starting the foreground service...")
        startWakeLock()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Log.i(TAG, "starting foreground process")
                currentServiceNotification = ServiceNotification(this, NOTIFICATION_ID, false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(NOTIFICATION_ID, currentServiceNotification!!.notification!!, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
                } else {
                    startForeground(NOTIFICATION_ID, currentServiceNotification!!.notification)
                }
                Log.i(TAG, "Starting foreground process successful!")
            } catch (e: Exception) {
                Log.e(TAG, "Error starting foreground process " + e.message)
            }
        }
        startCounter()
        return START_REDELIVER_INTENT
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.i(TAG, "on bind")
        return null
    }

    /**
     * it acquires the wakelock
     * This is not very simple to do. In theory we need a permanent wakelock but that is frowned upon
     * and I believe Android will kill the app that keeps the wakelock on
     * So we stop and start the wakelock every 2 hours and we keep it without wakelock for a second
     * Hopefully this will not cause any issues
     */
    private fun startWakeLock() {
        Log.i(TAG, "Acquiring the wakelock")
        val frequency = 120*60*1000L /*120 minutes*/
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
        wakeLockJob  = startRepeatingWakelockJob(frequency)
    }

    private fun startRepeatingWakelockJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                try {
                    wakeLock?.release()
                } catch (e: Exception) {
                    Log.i(TAG, "ignore: cannot release the wakelock")
                }
                Log.i(TAG, "Acquiring the wakelock again")
                // let's keep 1 second without a wakelock
                wakeLock?.acquire(timeInterval - 1000L)
                delay(timeInterval)
            }
        }
    }

    /**
     *  it starts the sensors
     */
    private fun startRepeatingCountingJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                incrementCounter()
                delay(timeInterval)
            }
        }
    }


    private fun incrementCounter(){
        Log.i(
            TAG,
            "Incrementing counter to value ${counter.value!! + 1}"
        )
        counter.postValue(counter.value!!+1)
        Log.i(TAG, "counter: ${counter.value}")
    }

    private fun startCounter() {
        repeatJob  = startRepeatingCountingJob(1000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "CounterService onDestroy")
        stopCounter()
        stopWakeLock()
    }

    /**
     * it stops all the sensors
     */
    private fun stopCounter() {
        Log.i(TAG, "stopping counter")
        try {
            if (repeatJob.isActive)
                repeatJob.cancel()
        } catch (e: Exception) {
            Log.i(TAG, "stop counter failed to stop" + e.message)
        }
    }

    private fun stopWakeLock(){
        Log.i(TAG, "stopping counter")
        try {
            if (wakeLockJob.isActive)
                wakeLockJob.cancel()
        } catch (e: Exception) {
            Log.i(TAG, "stop counter failed to stop" + e.message)
        }
        wakeLock?.release()
    }
}