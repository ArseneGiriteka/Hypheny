package com.leilaarsene.hypheny.model

import com.leilaarsene.hypheny.data.User
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserManager {
    fun getUserFromJSON(jsonObject: JSONObject): User {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

        val contactJson = jsonObject.getJSONObject("contacts")

        val contacts: MutableMap<String, List<String>> = mutableMapOf(
            "accepted" to List(
                contactJson.getJSONArray("accepted").length()
            ) { contactJson.getJSONArray("accepted").getString(it) },
            "blocked" to List(
                contactJson.getJSONArray("blocked").length()
            ) { contactJson.getJSONArray("blocked").toString(it) },
            "pending" to List(
                contactJson.getJSONArray("pending").length()
            ) { contactJson.getJSONArray("pending").toString(it) },
            "asked" to List(
                contactJson.getJSONArray("asked").length()
            ) { contactJson.getJSONArray("asked").toString(it) })

        val user = User(
            id = jsonObject.getString("id"),
            email = jsonObject.getString("email"),
            username = jsonObject.getString("username"),
            userProfile = jsonObject.optString("userProfile", null.toString()),
            contacts = contacts,
            bio = jsonObject.optString("bio", null.toString()),
            createdAt = dateFormat.parse(jsonObject.getString("created_at")) ?: Date(),
            modifiedAt = jsonObject.optString("modified_at")
                .takeIf { it != "null" && it.isNotEmpty() }
                ?.let { dateFormat.parse(it)},
            deletedAt = jsonObject.getString("deleted_at")
                .takeIf { it != "null" && it.isNotEmpty() }
                ?.let { dateFormat.parse(it) })

        return user
    }

    fun getUsersListFromJsonArray(jsonArray: JSONArray): List<User> {
        val usersList: MutableList<User> = mutableListOf()

        for (i in 0 until jsonArray.length()) {
            val jsonItem = jsonArray.getJSONObject(i)

            usersList.add(getUserFromJSON(jsonItem))
        }
        return usersList
    }
}