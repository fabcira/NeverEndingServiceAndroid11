package it.torino.workmanagerlabclass

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import it.torino.workmanagerlabclass.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG: String = javaClass.simpleName
    private val ACTIVITY_RECOGNITION_REQUEST_CODE: Int= 1013

    // this declares the layout - see the gradle file about using
    // buildFeatures { viewBinding true }
    // it transforms activity_main into ActivityMainBinding
    // https://developer.android.com/topic/libraries/view-binding
    private var binding: ActivityMainBinding? = null

    // Use the 'by viewModels()' Kotlin property delegate
    // from the activity-ktx artifact
    private val viewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This property is only valid between onCreateView and
        // onDestroyView.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initObservers()

        viewModel.startCounter()
    }

    /**
     * it initialises the view model and the methods used to retrieve the live data for the interface
     */
    private fun initObservers() {
        Log.i(TAG, "registering Observers: ViewModel? $viewModel")
        viewModel.currentCounter.observe(this) { value ->
            Log.i(TAG, "inserting value")
            binding?.value?.text = value.toString()
        }
    }

    /**
     * it initialises the view model and the methods used to retrieve the live data for the interface
     */
    private fun closeObservers() {
        Log.i(TAG, "registering Observers: ViewModel? $viewModel")
        viewModel.currentCounter.removeObservers (this)
    }
}