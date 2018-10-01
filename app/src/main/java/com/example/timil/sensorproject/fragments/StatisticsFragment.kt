package com.example.timil.sensorproject.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timil.sensorproject.R
import com.example.timil.sensorproject.database.ScoreDB
import com.example.timil.sensorproject.database.StepDB
import com.example.timil.sensorproject.entities.Score
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.statistics_fragment.*
import org.jetbrains.anko.doAsync
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
        graph.gridLabelRenderer.labelsSpace = 15
        graph.gridLabelRenderer.reloadStyles()

        doAsync {
            StepDB.get(context!!).stepDao().getAllSteps().observe(this@StatisticsFragment, android.arch.lifecycle.Observer {
                txtAllTimeSteps.text = it.toString()
            })
            getStepHistory(30)
        }
    }

    private fun getStepHistory(days: Int){
        val series = LineGraphSeries<DataPoint>()
        val mapLast = sortedMapOf<String, Int>()
        val mapThis = sortedMapOf<String, Int>()
        for (i in 1..days) {
            val split = date.minusDays(i.toLong()).format(formatter).split("-")

            if (split[1] == "0"+date.minusMonths(1).monthValue.toString()){
                mapLast[date.minusDays(i.toLong()).format(formatter)] = getSteps(date.minusDays(i.toLong()).format(formatter))
            }
            else if (split[1] == "0"+date.monthValue.toString()){
                mapThis[date.minusDays(i.toLong()).format(formatter)] = getSteps(date.minusDays(i.toLong()).format(formatter))
            }
        }
        mapLast.forEach { key, value ->
            val dateDB = LocalDate.parse(key, formatter)
            series.appendData(DataPoint(dateDB.toDate(), value.toDouble()), false, days)
        }
        mapThis.forEach { key, value ->
            val dateDB = LocalDate.parse(key, formatter)
            series.appendData(DataPoint(dateDB.toDate(), value.toDouble()), false, days)
        }

        graph.title = context!!.resources.getString(R.string.last_30_days)
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

    private fun getScore(){
        val score = ScoreDB.get(context!!).scoreDao().getScore()
        Log.d("TAG", "Score: $score")
    }

    /*
    private fun insertScore(level: Int, points: Int, trophies: Int){
        ScoreDB.get(context!!).scoreDao().insert(Score(0, level, points, trophies))
    }
    */

    private fun updateLevel(level: Int){
        ScoreDB.get(context!!).scoreDao().updateLevel(level)
    }

    private fun updatePoints(points: Int){
        ScoreDB.get(context!!).scoreDao().updatePoints(points)
    }

    private fun updateTrophyCount(count: Int){
        ScoreDB.get(context!!).scoreDao().updateTrophyCount(count)
    }
}