package com.example.timil.sensorproject

import android.arch.lifecycle.Observer
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timil.sensorproject.database.StepDB
import com.example.timil.sensorproject.entities.Step
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.home_fragment.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment: Fragment(), SensorEventListener {

    private lateinit var sm: SensorManager
    private var sStepDetector: Sensor? = null

    //@RequiresApi(Build.VERSION_CODES.O)
    private val date = LocalDateTime.now()
    //@RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    //@RequiresApi(Build.VERSION_CODES.O)
    private val formattedDate = date.format(formatter)

    private val map = hashMapOf<String, Int>()
    val series = LineGraphSeries<DataPoint>()

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.d("DBG", "Home fragment create view ")
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sm = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sStepDetector = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        // check there is step_detector sensor in the used device
        // if sensor exists, register listener and add observer
        // else inform user there is no sensor needed
        if (sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null ) {
            sm.registerListener(this, sStepDetector, SensorManager.SENSOR_DELAY_NORMAL)


            // TODO use preferences to let user chose step goal for the day
            StepDB.get(context!!).stepDao().getTodaysSteps(formattedDate).observe(this, Observer {
                txtStepsToday.text = it.toString()

                progressbar.progress = if (it != null)
                    it.toString().toInt() / 100
                else 0
            })

        }
        else {
            // TODO inform user there is no sensor and do something maybe
            Log.d("TAG", "You don't have required sensor(STEP_DETECTOR) in your phone!")
        }

        //set on click listener etc etc
        Log.d("DBG", "Home fragment view created ")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do stuff
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent) {
        saveSteps(formattedDate, getSteps(formattedDate) +1)
    }

    private fun saveSteps(sid: String, steps: Int){
        StepDB.get(context!!).stepDao().insert(Step(sid, steps))
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    private fun getStepHistory(days: Int){
        for (i in 1..days) {
            map.put(date.minusDays(i.toLong()).format(formatter), getSteps(date.minusDays(i.toLong()).format(formatter)))
        }
    }

    private fun getSteps(date: String): Int{
        when (StepDB.get(context!!).stepDao().getSteps(date)?.steps != null){
            true -> return StepDB.get(context!!).stepDao().getSteps(date)!!.steps
            false -> return 0
        }
    }
}