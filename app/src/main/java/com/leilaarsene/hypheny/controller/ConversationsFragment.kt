package com.leilaarsene.hypheny.controller

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.data.Conversation
import com.leilaarsene.hypheny.model.ApiService
import com.leilaarsene.hypheny.model.ConversationManager
import com.leilaarsene.hypheny.view.ConversationAdapter
import kotlinx.coroutines.*
import okhttp3.internal.notify
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConversationsFragment(private val activeUserId: String, private val context: Context): Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var conversations: List<Conversation>
    private val apiService = ApiService(this.context)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_conversations, container, false)

        conversations = listOf()
        recyclerView = view.findViewById(R.id.conversations_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        conversationAdapter = ConversationAdapter(activeUserId, conversations, context)
        recyclerView.adapter = conversationAdapter


        lifecycleScope.launch {
            conversations = findConversationsWithUserId(activeUserId)
            conversationAdapter = ConversationAdapter(activeUserId, conversations, context)
            recyclerView.adapter = conversationAdapter
        }

        return view
    }

    private suspend fun findConversationsWithUserId(userId: String): List<Conversation> {
        return suspendCoroutine { continuation ->
            apiService.findConversationsWithUserId(userId) { success, data ->
                if (success) {
                    val conversationList: List<Conversation> = data?.let {
                        ConversationManager().getConversationListFromJsonArray(JSONArray(it))
                    } ?: listOf()
                    continuation.resume(conversationList)
                } else {
                    continuation.resume(listOf())  // Resume with an empty list in case of failure
                }
            }
        }
    }
}