package com.example.timil.sensorproject.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.app.Fragment
import android.transition.TransitionManager
import android.view.*
import android.widget.*
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statistics_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(context!!, graphFormatter)
        graph.gridLabelRenderer.setHorizontalLabelsAngle(90)
        graph.gridLabelRenderer.labelsSpace = 20
        graph.title = context!!.resources.getString(R.string.last_30_days)
        graph.gridLabelRenderer.reloadStyles()

        doAsync {
            StepDB.get(context!!).stepDao().getAllSteps().observe(this@StatisticsFragment, Observer {
                txtAllTimeSteps.text = it.toString()
            })
            ScoreDB.get(context!!).scoreDao().getLiveScore().observe(this@StatisticsFragment, Observer { it ->
                doAsync {
                    if (it!![0].experience >= it[0].nextLevel){
                        updateNextLevel((it[0].nextLevel * 1.1).toInt())
                        updateLevel()
                    }
                }

                level.text = it!![0].level.toString()
                points.text = getString(R.string.expToNextLevel, it[0].experience, it[0].nextLevel)
                trophyCount.text = it[0].trophies.toString()
            })
        }

        // this had to be done in main thread. In async it would crash the app sometimes --> why?????
        val steps = StepDB.get(context!!).stepDao().getStepsList()
        getStepHistory(30, steps)

        stepsLinear.setOnClickListener {
            createPopupWindow(getString(R.string.stepsPopup), view)
        }
        trophyLinear.setOnClickListener {
            createPopupWindow(getString(R.string.trophyPopup),view)
        }
        pointsLinear.setOnClickListener {
            createPopupWindow(getString(R.string.pointsPopup),view)
        }
        levelLinear.setOnClickListener {
            createPopupWindow(getString(R.string.levelPopup),view)
        }
    }

    private fun getStepHistory(days: Int, stepData: List<Step>){
        val mapLast = sortedMapOf<String, Int>()
        val mapThis = sortedMapOf<String, Int>()

        for (i in 0 until stepData.size) {
            val key = date.minusDays(i.toLong()).format(formatter)
            val split = key.split("-")

            if (split[1].toInt() >= 10){
                if (split[1] == date.minusMonths(1).monthValue.toString()){
                    mapLast[key] = stepData[i].steps
                }
                else if (split[1] == date.monthValue.toString()){
                    mapThis[key] = stepData[i].steps
                }
            }
            else{
                if (split[1] == "0"+date.minusMonths(1).monthValue.toString()){
                    mapLast[key] = stepData[i].steps
                }
                else if (split[1] == "0"+date.monthValue.toString()){
                    mapThis[key] = stepData[i].steps
                }
            }
        }

        populateGraph(days, mapLast, mapThis)
    }

    private fun populateGraph(days: Int, mapLast: SortedMap<String, Int>, mapThis: SortedMap<String, Int>){
        val series = LineGraphSeries<DataPoint>()

        mapLast.forEach { key, value ->
            val dateDB = LocalDate.parse(key, formatter)
            series.appendData(DataPoint(dateDB.toDate(), value.toDouble()), false, days)
        }
        mapThis.forEach { key, value ->
            val dateDB = LocalDate.parse(key, formatter)
            series.appendData(DataPoint(dateDB.toDate(), value.toDouble()), false, days)
        }

        graph.gridLabelRenderer.numHorizontalLabels = days / 2
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMaxX(series.highestValueX)
        graph.viewport.setMinX(series.lowestValueX)
        graph.viewport.setMaxY(series.highestValueY + 3000)
        //graph.viewport.setMinY(series.lowestValueY - 3000)
        //graph.viewport.setMinY(series.lowestValueY)
        graph.viewport.setMinY(0.0)
        graph.addSeries(series)
    }

    private fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

    private fun updateLevel(){
        val level = ScoreDB.get(context!!).scoreDao().getScore()[0].level+1
        ScoreDB.get(context!!).scoreDao().updateLevel(level)
        ScoreDB.get(context!!).scoreDao().updateExperience(0)
    }

    private fun updateNextLevel(count: Int){
        ScoreDB.get(context!!).scoreDao().updateNextLevel(count)
    }

    @SuppressLint("InflateParams")
    private fun createPopupWindow(text: String, root: View){
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_layout, null)
        val tv = view.findViewById<TextView>(R.id.txtPopup)

        val popupWindow = PopupWindow(
                view,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )

        root.alpha = 0.3f
        tv.text = text
        popupWindow.elevation = 24.0F
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        view.setOnClickListener {
            popupWindow.dismiss()
        }

        TransitionManager.beginDelayedTransition(statisticsConstraint)
        popupWindow.showAtLocation(
                statisticsConstraint,
                Gravity.CENTER,
                0,
                -250
        )

        popupWindow.setOnDismissListener {
            root.alpha = 1.0f
        }
    }
}