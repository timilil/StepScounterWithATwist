package com.example.timil.sensorproject.models

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.timil.sensorproject.database.ScoreDB
import com.example.timil.sensorproject.entities.Score

class ScoreModel(application: Application): AndroidViewModel(application){
    private val score: List<Score> = ScoreDB.get(getApplication()).scoreDao().getScore()

    fun getScore() = score
}