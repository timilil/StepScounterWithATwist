package com.example.timil.sensorproject.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.example.timil.sensorproject.entities.Score

@Dao
interface ScoreDao{
    @Query("SELECT * FROM score")
    fun getScore(): List<Score>

    @Query("SELECT * FROM score")
    fun getLiveScore(): LiveData<List<Score>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(score: Score)

    @Query("UPDATE score SET level = :level")
    fun updateLevel(level: Int)

    @Query("UPDATE score SET experience = :experience")
    fun updateExperience(experience: Int)

    @Query("UPDATE score SET trophies = :trophies")
    fun updateTrophyCount(trophies: Int)

    @Query("UPDATE score SET nextLevel = :nextLevel")
    fun updateNextLevel(nextLevel: Int)

    @Update
    fun update(score: Score)
}