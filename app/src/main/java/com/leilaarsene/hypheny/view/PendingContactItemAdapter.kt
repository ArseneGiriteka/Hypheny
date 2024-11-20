package com.leilaarsene.hypheny.view

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.data.User
import com.leilaarsene.hypheny.model.ApiService

class PendingContactItemAdapter(private val activeUserId: String, private val pendingUsers :MutableList<User>, private val context: Context)
    :RecyclerView.Adapter<PendingContactItemAdapter.PendingContactItemViewHolder>(){
        class PendingContactItemViewHolder(itemView: View): ViewHolder(itemView) {
            val usernameTextView: TextView = itemView.findViewById(R.id.contact_item_username)
            val userProfileImage: ImageView = itemView.findViewById(R.id.contact_item_profile_picture)
            val acceptBtn: Button = itemView.findViewById(R.id.contact_item_add_friend_btn)
            val cancelBtn: ImageView = itemView.findViewById(R.id.cancel_contact_btn)
            val linearLayout: LinearLayout = itemView.findViewById(R.id.contact_activity_item)
        }

    private val apiService = ApiService(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingContactItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_contact_item, parent, false)
        return PendingContactItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pendingUsers.size
    }

    override fun onBindViewHolder(holder: PendingContactItemViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(pendingUsers[position].userProfile)
            .placeholder(R.drawable.user_profile)
            .into(holder.userProfileImage)

        holder.usernameTextView.text = pendingUsers[position].username
        holder.acceptBtn.setText("Accept")
        holder.acceptBtn.setOnClickListener {
            holder.acceptBtn.isEnabled = false
            apiService.acceptFriendship(activeUserId, pendingUsers[position].id) { success, message ->
                if (success) {
                    Log.d("FromPendingActivity", "$message")
                } else {
                    Log.d("FromPendingActivity", "$message")
                }
            }
            removeItem(position)
        }
        holder.cancelBtn.setOnClickListener { removeItem(position) }
    }

    private fun removeItem(position: Int) {
        pendingUsers.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, pendingUsers.size)
    }
}