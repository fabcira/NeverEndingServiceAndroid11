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
    val currentCounter: MutableLiveData<Int> = MutableLiveData()

    companion object{
        var myViewModel: MyViewModel? = null
    }


    fun startCounter() {
        myViewModel = this
        RestarterBroadcastReceiver.startWorker(context)
    }

}
