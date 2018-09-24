package com.example.timil.sensorproject.models

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class StepModel(application: Application): AndroidViewModel(application){
    //private val steps: LiveData<Step> = StepDB.get(getApplication()).stepDao().getAll()

    //fun getSteps() = steps
}