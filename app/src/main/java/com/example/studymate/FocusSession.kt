package com.example.studymate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tblFocusSession")
data class FocusSession(

    val taskName: String,
    val durationMinutes: Int,
    val sessionDate: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0

)