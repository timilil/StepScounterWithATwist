package com.example.timil.sensorproject.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.example.timil.sensorproject.entities.Step

@Dao
interface StepDao{
    @Query("SELECT SUM(steps) FROM step")
    fun getAllSteps(): LiveData<Int>

    @Query("SELECT * FROM step")
    fun getStepsList(): List<Step>

    @Query("SELECT * FROM step WHERE step.sid = :today")
    fun getSteps(today: String): Step?

    @Query("SELECT * FROM step WHERE step.sid = :today")
    fun getTodaysSteps(today: String): LiveData<Step>

    @Query("SELECT * FROM step WHERE steps=(SELECT MAX(steps) FROM step)")
    fun getHighestStep(): Step?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(step: Step)

    @Update
    fun update(step: Step)
}