package com.example.timil.sensorproject.models

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.timil.sensorproject.database.TrophyDB
import com.example.timil.sensorproject.entities.Trophy

class TrophyModel(application: Application): AndroidViewModel(application) {

    private val trophies: LiveData<List<Trophy>> = TrophyDB.get(getApplication()).trophyDao().getAll()

    fun getTrophies() = trophies
}