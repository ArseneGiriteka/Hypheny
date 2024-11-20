package com.leilaarsene.hypheny.data

import java.util.Date

data class Conversation(
    val id: String,
    val title: String="New Group",
    val members: List<String>,
    val admins: List<String>,
    val lastMessage: String="",
    val profilePicture: String?=null,
    val createdAt: Date = Date(),
    var modifiedAt: Date?,
    val deletedAt: Date?
)
