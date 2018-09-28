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
                sInstance =
                        Room.databaseBuilder(context.applicationContext,
                                StepDB::class.java, "step.db").build()
            }
            return sInstance!!
        }
    }
}