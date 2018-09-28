package com.example.timil.sensorproject.models

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.timil.sensorproject.database.StepDB
import com.example.timil.sensorproject.entities.Step

class StepModel(application: Application): AndroidViewModel(application){
    private val steps: List<Step?> = StepDB.get(getApplication()).stepDao().getAllSteps()

    fun getAllSteps() = steps
}