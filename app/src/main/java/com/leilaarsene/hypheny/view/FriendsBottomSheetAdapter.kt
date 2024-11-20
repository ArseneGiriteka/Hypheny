package com.leilaarsene.hypheny.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.leilaarsene.hypheny.R
import com.leilaarsene.hypheny.data.User
import com.bumptech.glide.Glide
import com.leilaarsene.hypheny.controller.MessageActivity

class FriendsBottomSheetAdapter(private val activeUserId: String, private val friends: List<User>, private val context: Context)
    :RecyclerView.Adapter<FriendsBottomSheetAdapter.FriendsBottomSheetViewHolder>(){

        class FriendsBottomSheetViewHolder(view: View): ViewHolder(view) {
            val profile: ImageView = view.findViewById(R.id.profile_picture)
            val username: TextView = view.findViewById(R.id.username_text)
            val messageBtn: ImageView = view.findViewById(R.id.message_btn)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsBottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friends_bottom_sheet_item, parent, false)
        return FriendsBottomSheetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: FriendsBottomSheetViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(friends[position].userProfile)
            .placeholder(R.drawable.user_profile)
            .into(holder.profile)

        holder.username.text = friends[position].username

        holder.messageBtn.setOnClickListener {  }

        holder.profile.setOnClickListener {  }

        holder.itemView.setOnClickListener {
        }
    }
}