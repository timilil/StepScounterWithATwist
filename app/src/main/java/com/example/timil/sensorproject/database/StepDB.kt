package com.example.timil.sensorproject.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.timil.sensorproject.daos.StepDao
import com.example.timil.sensorproject.entities.Step

@Database(entities = [(Step::class)],
        version = 1)

abstract class StepDB: RoomDatabase(){
    abstract fun stepDao(): StepDao

    companion object {
        private var sInstance: StepDB? = null
        @Synchronized
        fun get(context: Context): StepDB {
            if (sInstance == null){

                // allowMainThreadQueries = bad practise but it was the only way we could get the graph data
                // shown without sometimes crashing. See StatisticsFragment line 62-63
                // in homefragment highscore is fetched in mainThread, because using async sometimes freeze the app(in lines 43-47)
                sInstance =
                        Room.databaseBuilder(context.applicationContext,
                                StepDB::class.java, "step.db").allowMainThreadQueries().build()
            }
            return sInstance!!
        }
    }
}