package com.example.timil.sensorproject.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timil.sensorproject.R
import com.example.timil.sensorproject.database.StepDB
import com.example.timil.sensorproject.models.StepModel
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.statistics_fragment.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class StatisticsFragment: Fragment() {

    private val date = LocalDateTime.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val graphFormatter = SimpleDateFormat.getDateInstance()

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("DBG", "created statistics")
        return inflater.inflate(R.layout.statistics_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(context!!, graphFormatter)
        graph.gridLabelRenderer.setHorizontalLabelsAngle(90)
        graph.gridLabelRenderer.reloadStyles()

        val ump = ViewModelProviders.of(this).get(StepModel::class.java)
        val allSteps = ump.getAllSteps()
        var allTimeSteps = 0
        allSteps.forEach {
            allTimeSteps += it.toString().toInt()
        }

        txtAllTimeSteps.text = allTimeSteps.toString()

        getStepHistory(31)

    }

    private fun getStepHistory(days: Int){
        val series = LineGraphSeries<DataPoint>()
        val map = sortedMapOf<String, Int>()
        for (i in 1..days) {
            map[date.minusDays(i.toLong()).format(formatter)] = getSteps(date.minusDays(i.toLong()).format(formatter))
        }
        map.forEach { (key, value) ->
            val split = key.split("-")
            val dateDB = LocalDate.parse(key, formatter)
            if (split[1] == "0"+date.monthValue.toString()){
                series.appendData(DataPoint(dateDB.toDate(), value.toDouble()), false, days)
            }
        }

        graph.title = date.month.toString()
        graph.gridLabelRenderer.numHorizontalLabels = days / 2
        graph.viewport.setMaxX(series.highestValueX)
        graph.viewport.setMinX(series.lowestValueX)
        graph.addSeries(series)
    }

    private fun getSteps(date: String): Int {
        return when (StepDB.get(context!!).stepDao().getSteps(date)?.steps != null) {
            true -> StepDB.get(context!!).stepDao().getSteps(date)!!.steps
            false -> 0
        }
    }

    private fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}