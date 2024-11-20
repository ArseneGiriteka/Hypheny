package com.leilaarsene.hypheny.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ApiService(private val context: Context) {
    private val client = OkHttpClient()
    private val baseUrl = "http://10.0.2.2:5000"
    //private val baseUrl = "https://hypheny.ovh"

    private val sharedPreferences: SharedPreferences =
        this.context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun login(username: String, password: String, callback: (Boolean, Map<String, String?>?) -> Unit) {
        val url = "$baseUrl/login"

        val jsonObject = JSONObject()
        jsonObject.put("username", username)
        jsonObject.put("password", password)

        val body = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful) {

                    val sessionToken = responseData?.let { JSONObject(it).getString("access_token") }
                    val userData = responseData?.let { JSONObject(it).getString("user_data") }
                    val message = responseData?.let { JSONObject(it).getString("message") }
                    sharedPreferences.edit().putString("session_token", sessionToken).apply()
                    sharedPreferences.edit().putString("active_user_data", userData).apply()
                    callback(true, mapOf("userData" to userData, "message" to message))
                } else {
                    callback(false, null)
                }
            }
        })
    }

    fun signup(username: String, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        val url = "$baseUrl/register"

        val jsonObject = JSONObject()
        jsonObject.put("username", username)
        jsonObject.put("email", email)
        jsonObject.put("password", password)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, "Successful register!")
                } else {
                    callback(false, "Failed to register")
                }
            }
        })
    }

    fun searchUserByUsername(username: String, callback: (Boolean, Map<String, String?>?) -> Unit) {
        val url = "$baseUrl/search/user/username"
        val token = sharedPreferences.getString("session_token", null)

        if (token == null || !token.contains(".")) {
            Log.e("FromApiServiceAuthError", "Token is missing or malformed")
            Log.d("FromApiService", "Token: $token")
            return
        }

        val jsonObject = JSONObject()
        jsonObject.put("username", username)

        val body = jsonObject
            .toString().
            toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()

                if (response.isSuccessful) {
                    val userData = data?.let { JSONObject(it).optString("user_data") }
                    val message = data?.let { JSONObject(it).optString("message") }
                    Log.d("FromApiSearch", "message: $message/user_data: $userData")
                    callback(true, mapOf("userData" to userData, "message" to message))
                } else {
                    Log.d("FromApiSearch", "message: $data")
                    callback(false, null)
                }
            }
        })
    }

    fun findRandomUsers(id: String, callback: (Boolean, String?) -> Unit){
        val url = "$baseUrl/random/users"
        val token = sharedPreferences.getString("session_token", null)

        if (token == null || !token.contains(".")) {
            Log.e("FromApiServiceAuthError", "Token is missing or malformed")
            Log.d("FromApiService", "Token: $token")
            return
        }

        val jsonObject = JSONObject()
        jsonObject.put("id", id)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful) {
                    val message = data?.let { JSONObject(it).getString("message") }
                    val usersData = data?.let { JSONObject(it).getString("data") }
                    Log.d("FromApiService", "$message")
                    callback(true, usersData)
                } else {
                    callback(false, null)
                }
            }

        })
    }

    fun findUserByUserId(userId: String, callback: (Boolean, String?) -> Unit){
        val url = "$baseUrl/user/id"
        val token = sharedPreferences.getString("session_token", null)

        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $token").post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful) {
                    val userData = data?.let { JSONObject(it).getString("data") }
                    callback(true, userData)
                } else {
                    callback(false, null)
                }
            }

        })
    }

    fun updateUserProfile(userId: String, username: String, email: String, password: String, callback: (Boolean, String?, String?) -> Unit){
        val url = "$baseUrl/user/update"
        val token = sharedPreferences.getString("session_token", null)

        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("username", username)
        jsonObject.put("email", email)
        jsonObject.put("password", password)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $token").post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful) {
                    val message = data?.let { JSONObject(it).getString("message") }
                    val userJson = data?.let { JSONObject(it).getString("data") }
                    callback(true, message, userJson)
                } else {
                    val message = data?.let { JSONObject(it).getString("message") }
                    callback(true, message, null)
                }
            }

        })
    }

    fun getUsersToAdd(activeUserId: String, callback: (Boolean, String?, String?) -> Unit){
        val url = "$baseUrl/user/contacts/to_add"
        val token = sharedPreferences.getString("session_token",null)

        val jsonObject = JSONObject()
        jsonObject.put("userId", activeUserId)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $token").post(body).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful) {
                    val message = responseData?.let { JSONObject(it).getString("message") }
                    val usersToAddData = responseData?.let { JSONObject(it).getString("data") }
                    callback(true, message, usersToAddData)
                } else {
                    val message = try {
                        responseData?.let { JSONObject(it).getString("message") }
                    } catch (e: Exception) {
                        "${e.message}"
                    }
                    callback(false, message, "[]")
                }
            }

        })
    }

    fun requestFriendship(userId: String, targetId: String, callback: (Boolean, String?) -> Unit) {
        val url = "$baseUrl/user/ask-friendship"
        val token = sharedPreferences.getString("session_token", null)
        val dataJSONObject = JSONObject()
        dataJSONObject.put("userId", userId)
        dataJSONObject.put("targetId", targetId)

        val body = dataJSONObject.toString()
            .toRequestBody(
                "application/json; charset=utf-8".toMediaTypeOrNull()
            )

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()

                if (response.isSuccessful) {
                    val message = data?.let { JSONObject(it).getString("message") }
                    callback(true, message)
                } else {
                    callback(false, null)
                }
            }

        })
    }

    fun queryPendingContacts(userId: String, callback: (Boolean, String?) -> Unit) {
        val url = "$baseUrl/user/contacts/pending"
        val token = sharedPreferences.getString("session_token", null)

        if (token == null || !token.contains(".")) {
            Log.e("FromApiServiceAuthError", "Token is missing or malformed")
            Log.d("FromApiService", "Token: $token")
            return
        }

        val jsonObject = JSONObject()
        jsonObject.put("user_id", userId)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful) {
                    val message = data?.let { JSONObject(it).getString("message") }
                    Log.d("FromApiServiceQueryPendingContacts", "$message")
                    val pendingUsersData = data?.let { JSONObject(it).getString("data") }
                    callback(true, pendingUsersData)
                } else {
                    callback(false, null)
                }
            }

        })
    }

    fun findFriends(userId: String, callback: (Boolean, String?) -> Unit) {
        val url = "$baseUrl/user/contacts/accepted"
        val token = sharedPreferences.getString("session_token", null)

        if (token == null || !token.contains(".")) {
            return
        }

        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful) {
                    val message = data?.let { JSONObject(it).getString("message") }
                    val pendingUsersData = data?.let { JSONObject(it).getString("data") }
                    callback(true, pendingUsersData)
                } else {
                    callback(false, null)
                }
            }

        })
    }

    fun acceptFriendship(userId: String, targetId: String, callback: (Boolean, String?) -> Unit) {
        val url = "$baseUrl/user/accept-friendship"
        val token = sharedPreferences.getString("session_token", null)
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("targetId", targetId)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $token").post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful)
                {
                    val message = data?.let { JSONObject(it).getString("message") }
                    callback(true, message)
                } else {
                    val message = data?.let { JSONObject(it).getString("message") }
                    callback(false, message)
                }
            }

        })
    }

    fun findConversationsWithUserId(userId: String, callback: (Boolean, String?) -> Unit) {
        val url = "$baseUrl/user/conversation"
        val token = sharedPreferences.getString("session_token", null)

        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful) {
                    val message = data?. let { JSONObject(it).getString("message") }
                    val conversations = data?.let { JSONObject(it).getString("data") }
                    Log.d("FromApiServices", "data: $data")
                    callback(true, conversations)
                } else {
                    callback(true, null)
                }
            }

        })
    }

    fun getMessagesOfConversation(userId: String, conversationId: String, callback: (Boolean, String?) -> Unit){
        val url = "$baseUrl/user/conversation/get/messages"
        val token = sharedPreferences.getString("session_token", null)

        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("conversationId", conversationId)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $token").post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful) {
                    val message = data?.let { JSONObject(data).getString("message") }
                    val messagesData = data?.let { JSONObject(data).getString("data") }
                    Log.d("FromApiService", "Data messages of conversion: $data")
                    callback(true, messagesData)
                } else {
                    callback(false, null)
                }
            }

        })
    }

    fun createMessage(senderId: String, conversationId: String, messageType: String, messageBody: String, callback: (Boolean, String?) -> Unit) {
        val url = "$baseUrl/user/conversation/send/new_message"
        val token = sharedPreferences.getString("session_token", null)

        val jsonObject = JSONObject()
        jsonObject.put("senderId", senderId)
        jsonObject.put("conversationId", conversationId)
        jsonObject.put("messageType", messageType)
        jsonObject.put("body", messageBody)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $token").post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful) {
                    val message = data?.let { JSONObject(it).getString("message") }
                    callback(true, message)
                } else {
                    callback(false, null)
                }
            }

        })
    }

    fun findConversationsByConversationsId(conversationId: String, callback: (Boolean, String?) -> Unit){
        val url = "$baseUrl/conversation/id"
        val token = sharedPreferences.getString("session_token", null)

        val jsonObject = JSONObject()
        jsonObject.put("conversationId", conversationId)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder().url(url).addHeader("Authorization", "Bearer $token").post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                if (response.isSuccessful) {
                    val message = data?.let { JSONObject(it).getString("message") }
                    val conversationData = data?.let { JSONObject(it).getString("data") }
                    callback(true, conversationData)
                } else {
                    callback(false, null)
                }
            }

        })
    }

    fun isAuthenticated(): Boolean {
        val token = sharedPreferences.getString("session_token", null)
        return token != null
    }

    fun makeAuthorizedRequest(endpoint: String, callback: (Boolean, String?) -> Unit) {
        val token = sharedPreferences.getString("session_token", null)

        if (token == null) {
            callback(false, "No token found")
            return
        }

        val request = Request.Builder()
            .url("$baseUrl/$endpoint")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Request failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    callback(true, responseData)
                } else {
                    callback(false, "Request failed")
                }
            }
        })
    }

    fun logout() {
        sharedPreferences.edit().remove("session_token").apply()
    }
}