package com.leilaarsene.hypheny.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.data.User
import com.leilaarsene.hypheny.model.ApiService

class AddContactItemAdapter(private val activeUserId: String, private val contactsList: MutableList<User>, private val context: Context)
    : RecyclerView.Adapter<AddContactItemAdapter.AddContactItemViewHolder>(){

        class AddContactItemViewHolder(itemView: View): ViewHolder(itemView) {
            val username: TextView = itemView.findViewById(R.id.contact_item_username)
            val profilePicture: ImageView = itemView.findViewById(R.id.contact_item_profile_picture)
            val addBtn: Button = itemView.findViewById(R.id.contact_item_add_friend_btn)
            val cancelBtn: ImageView = itemView.findViewById(R.id.cancel_contact_btn)
        }

    private val apiService = ApiService(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_contact_item, parent, false)
        return AddContactItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: AddContactItemViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(contactsList[position].userProfile)
            .placeholder(R.drawable.user_profile)
            .into(holder.profilePicture)

        holder.username.text = contactsList[position].username
        holder.addBtn.setText("Add")
        holder.addBtn.setOnClickListener {
            holder.addBtn.isEnabled = false
            apiService.requestFriendship(activeUserId, contactsList[position].id) { success, message ->
                if (success) {
                    Log.d("FromAddContactItemAdapter", "$message")
                } else {
                    Log.d("FromAddContactItemAdapter", "$message")
                }
            }
            removeItem(position)
        }
        holder.cancelBtn.setOnClickListener {
            removeItem(position)
        }
    }

    private fun removeItem(position: Int) {
        contactsList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, contactsList.size)
    }
}