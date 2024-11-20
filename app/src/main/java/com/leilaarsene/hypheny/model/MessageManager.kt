package com.leilaarsene.hypheny.model

import com.leilaarsene.hypheny.data.Message
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageManager {
    fun getMessageFromJsonObject(jsonObject: JSONObject): Message {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())

        val id = jsonObject.getString("id")
        val conversationId = jsonObject.getString("conversationId")
        val senderId = jsonObject.getString("senderId")
        val body = jsonObject.getString("body")
        val messageType = jsonObject.getString("messageType")
        val emotionalFlag = jsonObject.optString("emotionalFlag")
        val createdAt = dateFormat.parse(jsonObject.optString("createdAt")) ?: Date()
        val modifiedAt = jsonObject.optString("modifiedAt").takeIf { !it.isNullOrEmpty() && it != "null" }?.let { dateFormat.parse(it) }
        val deleteAt = jsonObject.optString("deleteAt").takeIf { !it.isNullOrEmpty() && it != "null" }?.let { dateFormat.parse(it) }

        return Message(
            id=id,
            conversationId=conversationId,
            senderId=senderId,
            body=body,
            messageType=messageType,
            emotionFlag=emotionalFlag,
            createdAt=createdAt,
            modifiedAt=modifiedAt,
            deletedAt=deleteAt
        )
    }

    fun getMessagesFromJsonArray(jsonArray: JSONArray): List<Message> {
        val messages = mutableListOf<Message>()

        for (i in 0 until jsonArray.length()) {
            val jsonItem = jsonArray.getJSONObject(i)
            messages.add(getMessageFromJsonObject(jsonItem))
        }
        return  messages
    }
}