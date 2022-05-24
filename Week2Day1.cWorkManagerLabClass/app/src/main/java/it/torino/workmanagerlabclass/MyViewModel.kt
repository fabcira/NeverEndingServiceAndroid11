package it.torino.workmanagerlabclass


import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope


class MyViewModel(application: Application) : AndroidViewModel(application) {
    var TAG = this::class.simpleName
    private val context: Context = application
    // this will track the value of the counter in the Service and report it to the MainActivity
    var currentCounter: MutableLiveData<Int> = CounterService.counter

    fun startCounter() {
        currentCounter = CounterService.counter
        RestarterBroadcastReceiver.startWorker(context)
    }

}
