package com.leilaarsene.hypheny.controller

import BaseActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.data.User
import com.leilaarsene.hypheny.model.UserManager
import com.leilaarsene.hypheny.view.PendingContactItemAdapter
import org.json.JSONArray

class FriendsActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_activity)

        val recyclerView = findViewById<RecyclerView>(R.id.pending_contacts_recycler_view)
        val usersData = intent.getStringExtra("usersData")
        Log.d("FromPendingContactActivity", "$usersData")

        val usersDataJsonString = "{\"usersData\": $usersData}"

        val pendingUsers: List<User> = if (usersDataJsonString.isNotEmpty()) {
            UserManager().getUsersListFromJsonArray(JSONArray(usersData))
        } else {
            listOf()
        }
        Log.d("FromPendingContactActivity", "$pendingUsers")

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = activeUser?.let { PendingContactItemAdapter(it.id, pendingUsers.toMutableList(), this) }
    }
}