package com.example.timil.sensorproject.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timil.sensorproject.R
import com.example.timil.sensorproject.database.StepDB
import kotlinx.android.synthetic.main.home_fragment.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment: Fragment() {

    private val date = LocalDateTime.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val formattedDate = date.format(formatter)

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // TODO use preferences to let user chose step goal for the day
        StepDB.get(context!!).stepDao().getTodaysSteps(formattedDate).observe(this, Observer {
            txtStepsToday.text = it.toString()
            progressbar.progress = if (it != null)
                it.toString().toInt() / 100
            else 0
        })

        Log.d("DBG", "Home fragment create view ")
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set on click listener etc etc
        Log.d("DBG", "Home fragment view created ")
    }
}