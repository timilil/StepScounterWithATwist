package com.example.timil.sensorproject.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Trophy(
        @PrimaryKey(autoGenerate = true)
        val trophyid: Long,
        val latitude: Double,
        val longitude: Double) {
    override fun toString(): String = "$latitude $longitude"
}