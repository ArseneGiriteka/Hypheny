package com.leilaarsene.hypheny.data

import java.util.Date
import java.util.Dictionary

data class Message(
    val id: String,
    val senderId: String,
    val conversationId: String,
    val messageType: String,
    val body: String,
    val emotionFlag: String?,
    val createdAt: Date = Date(),
    var modifiedAt: Date?,
    val deletedAt: Date?,
)