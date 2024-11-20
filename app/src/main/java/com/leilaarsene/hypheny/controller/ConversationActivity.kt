package com.leilaarsene.hypheny.controller

import BaseActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.data.Conversation
import com.leilaarsene.hypheny.data.User
import com.leilaarsene.hypheny.model.ApiService
import com.leilaarsene.hypheny.model.ConversationManager
import com.leilaarsene.hypheny.model.UserManager
import com.leilaarsene.hypheny.view.ConversationAdapter
import com.leilaarsene.hypheny.view.FriendsBottomSheetAdapter
import kotlinx.coroutines.*
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConversationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)


        activeUser?.let {
            Log.d("FromConversationActivity", "call conversation fragment")
            supportFragmentManager.beginTransaction().replace(R.id.active_fragment_container, ConversationsFragment(it.id, this)).commit()
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_conversations -> {
                    supportFragmentManager.beginTransaction().replace(R.id.active_fragment_container, ConversationsFragment(activeUser!!.id,this)).commit()
                    true
                }

                R.id.nav_friends -> {
                    showFriendsBottomSheet()
                    true
                }

                R.id.nav_new_contacts -> {
                    supportFragmentManager.beginTransaction().replace(R.id.active_fragment_container, ContactsFragments(activeUser!!.id,this)).commit()
                    true
                }

                R.id.more_options -> {
                    showMoreOptionsBottomSheet()
                    true
                }
                else -> false
            }
        }

    }

    private fun showMoreOptionsBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_more, null)
        bottomSheetDialog.setContentView(view)

        view.findViewById<View>(R.id.user_profile).setOnClickListener {
            bottomSheetDialog.dismiss()
            if (activeUser != null) {
                supportFragmentManager.beginTransaction().replace(R.id.active_fragment_container, EditProfileFragment(activeUser!!.id, this)).commit()
            }
        }

        view.findViewById<View>(R.id.logout).setOnClickListener {
            bottomSheetDialog.dismiss()
            this.logout()
        }

        bottomSheetDialog.show()
    }

    private fun showFriendsBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.friends_bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.friends_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = FriendsBottomSheetAdapter(activeUser!!.id, listOf(), this)

        lifecycleScope.launch {
            val friends = getFriends()
            recyclerView.adapter = FriendsBottomSheetAdapter(activeUser!!.id, friends, this@ConversationActivity)
            bottomSheetDialog.show()
        }
        bottomSheetDialog.show()
    }

    private suspend fun getFriends(): List<User> {
        val apiService = ApiService(this)
        return suspendCoroutine { continuation ->
            apiService.findFriends(activeUser!!.id) { success, data ->
                if (success) {
                    val listOfUsers = UserManager().getUsersListFromJsonArray(JSONArray(data!!)) ?: listOf()
                    continuation.resume(listOfUsers)
                }else continuation.resume(listOf())
            }
        }
    }
}