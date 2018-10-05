package com.example.timil.sensorproject.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Score(
        @PrimaryKey
        val id: Int,
        val level: Int,
        val experience: Int,
        val trophies: Int,
        val nextLevel: Int
)
{
    override fun toString(): String = "$id $level $experience $trophies $nextLevel"
}