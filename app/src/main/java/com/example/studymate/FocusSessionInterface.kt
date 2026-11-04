package com.example.studymate

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FocusSessionInterface {

    @Insert
    suspend fun addSession(session: FocusSession)

    @Query("SELECT* FROM tblFocusSession ORDER BY id DESC")
    fun readAllSessions(): LiveData<List<FocusSession>>

    @Query("SELECT COUNT(*) FROM tblFocusSession")
    fun getTotalSessions(): LiveData<Int>

    @Query("SELECT SUM(durationMinutes) FROM tblFocusSession")
    fun getTotalFocusMinutes(): LiveData<Int?>

}