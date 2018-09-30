package com.example.timil.sensorproject.models

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.timil.sensorproject.database.StepDB

class StepModel(application: Application): AndroidViewModel(application){
    private val steps: LiveData<Int> = StepDB.get(getApplication()).stepDao().getAllSteps()

    fun getAllSteps() = steps
}