package com.example.timil.sensorproject.fragments

import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timil.sensorproject.GOAL_PREF
import com.example.timil.sensorproject.R
import com.example.timil.sensorproject.database.StepDB
import com.example.timil.sensorproject.entities.Step
import kotlinx.android.synthetic.main.home_fragment.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.logging.SimpleFormatter

class HomeFragment: Fragment() {

    private val date = LocalDateTime.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val formattedDate = date.format(formatter)
    private val simpleFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    private val simpleDate = date.format(simpleFormatter)
    private var pref: SharedPreferences? = null

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        pref = PreferenceManager.getDefaultSharedPreferences(context)
        StepDB.get(context!!).stepDao().getTodaysSteps(formattedDate).observe(this, Observer {
            txtStepsToday.text = it.toString()
            progressbar.progress = if (it != null)
                it.toString().toInt() / pref!!.getString(GOAL_PREF, "N/A").toInt()
            else 0
        })

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set on click listener etc etc
        Log.d("DBG", "some: $simpleDate date: $date")
        val dateSome = LocalDateTime.parse(getHighScore().sid+"T00:00:00.000")
        txtHighScore.text = "High score: ${dateSome.format(simpleFormatter)} ${getHighScore().steps}"

        Log.d("DBG", "Home fragment view created ")
    }

    private fun getHighScore(): Step{
       return StepDB.get(context!!).stepDao().getHighestStep()
    }
}