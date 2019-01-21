package com.amra.android.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert

@Dao
interface TaskDao {

    @Insert
    fun insert(task: Task): Long

    @Insert
    fun insert(tasks: List<Task>): List<Long>

}