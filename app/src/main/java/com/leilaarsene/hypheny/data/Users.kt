package com.leilaarsene.hypheny.data

import android.text.format.DateFormat
import java.util.Date
import java.util.Dictionary

data class User(
    val id: String = "",
    val email: String,
    val username: String,
    val userProfile: String? = "",
    val contacts: MutableMap<String, List<String>>? = null,
    val bio: String? = "",
    val createdAt: Date,
    var modifiedAt: Date?,
    val deletedAt: Date?
)