package com.example.timil.sensorproject.fragments

import android.os.Bundle
import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timil.sensorproject.R
import com.example.timil.sensorproject.database.ScoreDB
import com.example.timil.sensorproject.database.StepDB
import com.example.timil.sensorproject.entities.Step
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
            StepDB.get(context!!).stepDao().getAllSteps().observe(this@StatisticsFragment, Observer {
                txtAllTimeSteps.text = it.toString()
            })
            ScoreDB.get(context!!).scoreDao().getLiveScore().observe(this@StatisticsFragment, Observer { it ->
                doAsync {
                    if (it!![0].points >= it[0].nextLevel){
                        updateNextLevel((it[0].nextLevel * 1.1).toInt())
                        updateLevel()
                    }
                }

                level.text = it!![0].level.toString()
                points.text = it[0].points.toString()+" / "+it[0].nextLevel.toString()
                trophyCount.text = it[0].trophies.toString()
            })
            /*val steps = StepDB.get(context!!).stepDao().getStepsList()
            UI {
                getStepHistory(30, steps)
            }*/
        }

        // this had to be done in main thread. In async it would crash the app sometimes --> why?????
        val steps = StepDB.get(context!!).stepDao().getStepsList()
        getStepHistory(30, steps)

    }

    /*private fun saveSteps(sid: String, steps: Int){
        StepDB.get(context!!).stepDao().insert(Step(sid, steps))
    }*/

    private fun getStepHistory(days: Int, stepData: List<Step>){
        val series = LineGraphSeries<DataPoint>()
        val mapLast = sortedMapOf<String, Int>()
        val mapThis = sortedMapOf<String, Int>()

        for (i in 0 until stepData.size) {
            val key = date.minusDays(i.toLong()).format(formatter)
            val split = key.split("-")

            if (split[1].toInt() >= 10){
                Log.d("DBG", "Split >= 10 $split")
                if (split[1] == date.minusMonths(1).monthValue.toString()){
                    //Log.d("DBG", "KEY IS  "+date.minusDays(i.toLong()).format(formatter))
                    mapLast[key] = stepData[i].steps//getSteps(date.minusDays(i.toLong()).format(formatter))
                }
                else if (split[1] == date.monthValue.toString()){
                    mapThis[key] = stepData[i].steps
                }
            }
            else{
                Log.d("DBG", "Split else $split")
                if (split[1] == "0"+date.minusMonths(1).monthValue.toString()){
                    //Log.d("DBG", "KEY IS  "+date.minusDays(i.toLong()).format(formatter))
                    mapLast[key] = stepData[i].steps//getSteps(date.minusDays(i.toLong()).format(formatter))
                }
                else if (split[1] == "0"+date.monthValue.toString()){
                    mapThis[key] = stepData[i].steps
                }
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
        /*graph.post {
            graph.removeAllSeries()
            graph.addSeries(series)
        }*/
    }

    private fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

    private fun updateLevel(){
        val level = ScoreDB.get(context!!).scoreDao().getScore()[0].level+1
        ScoreDB.get(context!!).scoreDao().updateLevel(level)
        ScoreDB.get(context!!).scoreDao().updatePoints(0)
    }

    private fun updateNextLevel(count: Int){
        ScoreDB.get(context!!).scoreDao().updateNextLevel(count)
    }
}