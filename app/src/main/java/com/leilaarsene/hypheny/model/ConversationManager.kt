package com.leilaarsene.hypheny.model

import com.leilaarsene.hypheny.data.Conversation
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConversationManager {
    fun getConversationFromJsonObject(jsonObject: JSONObject): Conversation{

        //val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())

        val id = jsonObject.getString("id")
        val title = jsonObject.optString("title", "new conversation")
        val lastMassage = jsonObject.optString("lastMessage")
        val membersJson = jsonObject.getJSONArray("members")
        val members: MutableList<String> = mutableListOf()
        for (i in 0 until membersJson.length()) {
            members.add(membersJson.getString(i))
        }

        val adminsJson = jsonObject.getJSONArray("admins")
        val admins: MutableList<String> = mutableListOf()
        for (i in 0 until adminsJson.length()) {
            admins.add(adminsJson.getString(i))
        }

        val profilePicture = jsonObject.optString("profile", null.toString())
        val createdAt = dateFormat.parse(jsonObject.getString("created_at")) ?: Date()
        val modifiedAt = jsonObject.optString("modified_at").takeIf { !it.isNullOrEmpty() && it != "null" }?.let { dateFormat.parse(it) }
        val deletedAt = jsonObject.optString("deleted_at").takeIf { !it.isNullOrEmpty()  && it != "null" }?.let { dateFormat.parse(it) }

        return Conversation(
            id=id,
            title=title,
            members=members,
            admins=admins,
            lastMessage=lastMassage,
            profilePicture=profilePicture,
            createdAt=createdAt,
            modifiedAt=modifiedAt,
            deletedAt=deletedAt
        )
    }

    fun getConversationListFromJsonArray(jsonArray: JSONArray): List<Conversation>{
        val conversations: MutableList<Conversation> = mutableListOf()
        for (i in 0 until jsonArray.length()) {
            conversations.add(getConversationFromJsonObject(jsonArray.getJSONObject(i)))
        }
        return conversations
    }
}