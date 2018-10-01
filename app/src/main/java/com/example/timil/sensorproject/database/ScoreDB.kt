package com.example.timil.sensorproject.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.timil.sensorproject.daos.ScoreDao
import com.example.timil.sensorproject.entities.Score

@Database(entities = [(Score::class)],
        version = 1)

abstract class ScoreDB: RoomDatabase(){
    abstract fun scoreDao(): ScoreDao

    companion object {
        private var sInstance: ScoreDB? = null
        @Synchronized
        fun get(context: Context): ScoreDB {
            if (sInstance == null){
                sInstance =
                        Room.databaseBuilder(context.applicationContext,
                                ScoreDB::class.java, "score.db").build()
            }
            return sInstance!!
        }
    }
}