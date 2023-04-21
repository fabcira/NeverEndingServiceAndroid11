package it.torino.workmanagerlabclass


import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope


class MyViewModel(private val application: Application) : AndroidViewModel(application) {
    // this will track the value of the counter in the Service and report it to the MainActivity
    var currentCounter: MutableLiveData<Int> = CounterService.counter

    fun startCounter() {
        RestarterBroadcastReceiver.startWorker(application)
    }

}
