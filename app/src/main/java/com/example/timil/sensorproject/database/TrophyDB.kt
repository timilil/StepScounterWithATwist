package com.example.timil.sensorproject.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.timil.sensorproject.daos.TrophyDao
import com.example.timil.sensorproject.entities.Trophy

@Database(entities = [(Trophy::class)], version = 1)
abstract class TrophyDB: RoomDatabase() {

    abstract fun trophyDao(): TrophyDao

    companion object {
        private var sInstance: TrophyDB? = null
        @Synchronized
        fun get(context: Context): TrophyDB {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.applicationContext, TrophyDB::class.java, "trophy.db").build()
            }
            return sInstance!!
        }
    }

}