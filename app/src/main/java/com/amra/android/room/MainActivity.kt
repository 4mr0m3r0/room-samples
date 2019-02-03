package com.amra.android.room

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.LinearLayout
import android.widget.TextView
import com.amra.android.room.DatabaseDataHolder.Companion.groupTasks
import com.amra.android.room.users.UsersActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var taskListAdapter: TaskListAdapter
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        database = AppDatabase.getInstance(this)
        taskDao = database.taskDao()

        addTaskButton.setOnClickListener {
            addTask()
        }

        taskTitleInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == IME_ACTION_DONE) {
                addTask()
                taskTitleInput.setText("")
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })

        taskListAdapter = TaskListAdapter {
            val taskId = it.task.id
            startActivity(TaskDetailsActivity.launchIntent(this, taskId))
        }
        taskList.layoutManager = LinearLayoutManager(this)
        taskList.adapter = taskListAdapter

        setLiveDataObserver()
    }

    private fun setLiveDataObserver() {
        taskDao.getAllWithAssignedUsers().observe(this, Observer {assignedTasks ->
            if (assignedTasks == null) {
                return@Observer
            }

            val grouped = DatabaseDataHolder.groupTasks(assignedTasks)
            taskListAdapter.submitList(grouped)
        })
    }

    private fun addTask() {
        val title = taskTitleInput.text.toString()

        if (title.isBlank()) {
            Snackbar.make(toolbar, "Task title is required", Snackbar.LENGTH_SHORT).show()
            return
        }

        val task = Task(title = title)

        thread {
            taskDao.insert(task)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menuItemManageUsers -> {
                startActivity(Intent(this, UsersActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}