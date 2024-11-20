package com.leilaarsene.hypheny.controller

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.data.Message
import com.leilaarsene.hypheny.model.ApiService
import com.leilaarsene.hypheny.model.MessageManager
import com.leilaarsene.hypheny.view.MessageAdapter
import org.json.JSONArray

class MessageActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_activity)

        val apiService = ApiService(this)
        val conversationId = intent.getStringExtra("conversationId")?: ""

        val profile = findViewById<ImageView>(R.id.profile_picture)
        val conversationTitle = findViewById<TextView>(R.id.conversation_title)
        val messagesRecyclerView = findViewById<RecyclerView>(R.id.message_recycler_view)
        val newMessageEditText = findViewById<EditText>(R.id.new_message)
        val sendButton = findViewById<Button>(R.id.send_btn)

        var messages = listOf<Message>()

        conversationTitle.text = intent.getStringExtra("conversationTitle")
        Glide.with(this).load("").placeholder(R.drawable.user_profile).into(profile)

        activeUser?.let {
            apiService.getMessagesOfConversation(it.id, conversationId) { success, data ->
                if (success) {
                    messages = MessageManager().getMessagesFromJsonArray(JSONArray(data))
                    runOnUiThread {
                        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
                        messagesRecyclerView.adapter = MessageAdapter(it.id, messages, this)
                    }
                }
            }
        }

        sendButton.setOnClickListener {
            Log.d("FromMessageActivity", "New Message: ${newMessageEditText.text}")
            if (newMessageEditText.text.toString() != "") {
                if (newMessageEditText.text.isNotEmpty()) {
                    activeUser?.let { it1 ->
                        apiService.createMessage(it1.id,conversationId,"text",newMessageEditText.text.toString()) { success, data ->
                            if (success) {
                                apiService.getMessagesOfConversation(activeUser!!.id, conversationId) { ok, msgData ->
                                    if (ok) {
                                        runOnUiThread {
                                            messages = MessageManager().getMessagesFromJsonArray(JSONArray(msgData))
                                            messagesRecyclerView.adapter = MessageAdapter(activeUser!!.id , messages, this)
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                newMessageEditText.setText("")
            }
        }
    }
}