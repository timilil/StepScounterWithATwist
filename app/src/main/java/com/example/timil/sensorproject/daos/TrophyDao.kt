package com.example.timil.sensorproject.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.example.timil.sensorproject.entities.Trophy

@Dao
interface TrophyDao {

    @Query("SELECT * FROM trophy")
    fun getAll(): LiveData<List<Trophy>>

    @Query("SELECT * FROM trophy")
    fun getAllOld(): List<Trophy>

    @Delete
    fun delete(trophy: Trophy)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trophy: Trophy)

    @Update
    fun update(trophy: Trophy)

}