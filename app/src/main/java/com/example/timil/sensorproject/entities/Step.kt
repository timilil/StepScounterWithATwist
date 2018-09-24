package com.example.timil.sensorproject.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Step(
        @PrimaryKey
        val sid: String,
        val steps: Int
)
{
    override fun toString(): String = "$steps"
}