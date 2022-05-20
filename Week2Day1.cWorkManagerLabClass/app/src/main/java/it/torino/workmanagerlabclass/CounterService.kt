/*
 * Copyright (c) Code Developed by Prof. Fabio Ciravegna
 * All rights Reserved
 */
package it.torino.workmanagerlabclass

import android.app.Service
import android.content.Intent
import android.os.*
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.*


class CounterService : Service() {
    private var wakeLock: WakeLock? = null
    private var currentServiceNotification: ServiceNotification? = null
    private val TAG = CounterService::class.java.simpleName
    private var counter: Int = 0
    private var handler: Handler? = null
    private lateinit var timeoutRunnable: Runnable

    companion object {
        var currentService: CounterService? = null
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
                startForeground(NOTIFICATION_ID, currentServiceNotification!!.notification)
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
     */
    private fun startWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
        // you must acquire a wake lock in order to keep the service going
        wakeLock?.acquire()
    }

    /**
     *  it starts the sensors
     */
    private fun startCounter() {
        Log.i(TAG, "Starting the counter...")
        val looper: Looper? = Looper.myLooper()
        looper.let {
            handler = Handler(looper!!)
            timeoutRunnable = Runnable {
                counter++
                Log.i(TAG, "counter: $counter")
                saveCounterInLiveData(counter)
                handler?.postDelayed(timeoutRunnable, 3000)
            }
            handler?.post(timeoutRunnable)
        }

    }

    private fun saveCounterInLiveData(counter: Int) {
        MyViewModel.myViewModel?.viewModelScope?.launch(Dispatchers.IO) {
            insertCount(counter)
        }
    }

    private suspend fun insertCount(counter: Int) {
        withContext(Dispatchers.Main) {
            MyViewModel.myViewModel?.currentCounter?.value= counter
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "CounterService Ondestroy")
        stopCounter()
        if (wakeLock != null)
            wakeLock!!.release()
    }

    /**
     * it stops all the sensors
     */
    private fun stopCounter() {
        Log.i(TAG, "stopping counter")
        try {

        } catch (e: Exception) {
            Log.i(TAG, "stop counter failed to stop" + e.message)
        }

    }




}