package com.amra.android.room.users

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.amra.android.room.AppDatabase
import com.amra.android.room.DatabaseDataHolder
import com.amra.android.room.R
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.content_users.*
import kotlin.concurrent.thread

class UsersActivity : AppCompatActivity() {
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        database = AppDatabase.getInstance(this)
        userDao = database.userDao()


        addUserButton.setOnClickListener {
            addUser()
            userNameInput.setText("")
        }

        userNameInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addUser()
                userNameInput.setText("")
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })

        userListAdapter = UserListAdapter {
            thread { userDao.delete(it.user) }
        }

        userList.layoutManager = LinearLayoutManager(this)
        userList.adapter = userListAdapter


        userDao.getAllWithAssignedTasks().observe(this, Observer { usersAndTasks ->
            if (usersAndTasks == null) {
                return@Observer
            }

            val items = DatabaseDataHolder.groupUsers(usersAndTasks)
            userListAdapter.submitList(items)
        })
    }

    private fun addUser() {
        val title = userNameInput.text.toString()

        if (title.isBlank()) {
            Snackbar.make(toolbar, "User's name is required", Snackbar.LENGTH_SHORT).show()
            return
        }

        val user = User(name = title)

        thread {
            userDao.insert(user)
        }
    }
}