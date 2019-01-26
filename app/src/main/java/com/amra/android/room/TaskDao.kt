package com.amra.android.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface TaskDao {

    @Insert
    fun insert(task: Task): Long

    @Insert
    fun insert(tasks: List<Task>): List<Long>

    @Query("SELECT * FROM Task")
    fun getAll(): LiveData<List<Task>>

    @Query("SELECT * FROM Task WHERE id = :taskId")
    fun getTask(taskId: Int): LiveData<Task>

    @Delete
    fun delete(task: Task)

    @Query("DELETE FROM Task WHERE id = :taskId")
    fun delete(taskId: Int)

    @Update
    fun update(task: Task)

    @Query("UPDATE Task SET title=:taskTitle, completed=:taskIsCompleted WHERE id=:taskId")
    fun update(taskId: Int, taskTitle: String, taskIsCompleted: Boolean)

}