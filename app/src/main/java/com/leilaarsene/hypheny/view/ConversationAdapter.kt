package com.leilaarsene.hypheny.view

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.controller.MessageActivity
import com.leilaarsene.hypheny.data.Conversation
import com.leilaarsene.hypheny.model.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ConversationAdapter(private val activeUserId: String, private val conversationsList: List<Conversation>, private val context: Context) :

    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {
        class ConversationViewHolder(itemView: View): ViewHolder(itemView) {
            val profilePicture: ImageView = itemView.findViewById(R.id.profile_picture)
            val conversationTitle: TextView = itemView.findViewById(R.id.conversation_title)
            val lastMessage: TextView = itemView.findViewById(R.id.last_message)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conversation_item_view, parent, false)
        return ConversationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return conversationsList.size
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.conversationTitle.text = "Loading ..."
        val apiService = ApiService(context)
        var Id = ""
        var text = "Loading ..."
        for (i in 0 until conversationsList[position].members.size) {
            if (conversationsList[position].members[i] != activeUserId)
            {
                Id = conversationsList[position].members[i]
                break
            }
        }



//        apiService.findUserByUserId(Id) { success, data ->
//            if (success) {
//                text = data?.let { JSONObject(it).getString("username") }.toString()
//                Log.d("FromConversationAdapter", "conversation name $text")
//            }
//        }
        //holder.conversationTitle.text = conversationsList[position].title
        holder.lastMessage.text = conversationsList[position].lastMessage

        Glide.with(holder.itemView)
            .load(conversationsList[position].profilePicture ?: "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fartprojectsforkids.org%2Fwp-content%2Fuploads%2F2021%2F01%2FRubber-Ducky.jpeg&f=1&nofb=1&ipt=0d3af0a72ab4a6262184a35328c840a5c653cc8bf8160ea5b0ce5f215d61052f&ipo=images")
            .placeholder(R.drawable.user_profile)
            .into(holder.profilePicture)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("conversationId", conversationsList[position].id)
            intent.putExtra("conversationTitle", holder.conversationTitle.text)
            context.startActivity(intent)
        }

        CoroutineScope(Dispatchers.Main).launch {
            val username = findUsernameWithUserId(Id)
            holder.conversationTitle.text = username
        }

        Log.d("FromConversationAdapter", "conversations $conversationsList")
        Log.d("FromConversationAdapter", "Binding item at position: $position with title: ${conversationsList[position].title}")
    }

    private suspend fun findUsernameWithUserId(userId: String): String {
        val apiService = ApiService(context)
        return suspendCoroutine { continuation ->
            apiService.findUserByUserId(userId) { success, data ->
                if (success) {
                    val username = data?.let {
                        JSONObject(it).getString("username").toString()
                    } ?: "new conversation"
                    continuation.resume(username)
                } else {
                    continuation.resume("failed to get this")
                }
            }
        }
    }
}