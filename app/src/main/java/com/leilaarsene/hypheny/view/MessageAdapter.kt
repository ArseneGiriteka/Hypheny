package com.leilaarsene.hypheny.view

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.controller.MessageActivity
import com.leilaarsene.hypheny.data.Message

class MessageAdapter(private val activeUserId: String, private val messages: List<Message>, private val context: Context): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val messageView: TextView = itemView.findViewById(R.id.message_text)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linear_layout_message_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_view, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.messageView.text = messages[position].body
        if (activeUserId == messages[position].senderId) {
            holder.messageView.apply {
                gravity = Gravity.END
            }
            //messages[position].emotionFlag?.let { holder.messageView.setBackgroundColor(it.toInt(10)) }
            holder.linearLayout.apply {
                gravity = Gravity.END
            }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, MessageActivity::class.java)
            startActivity(context, intent, null)
        }
    }
}