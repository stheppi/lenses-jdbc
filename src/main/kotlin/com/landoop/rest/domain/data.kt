package com.landoop.rest.domain

data class Message(
    val timestamp: Long,
    val partition: Int,
    val key: String,
    val offset: Long,
    val topic: String,
    val value: String)

data class JdbcData(
    val topic: String?,
    val data: List<String>,
    val schema: String?
)

data class InsertResponse(val name: String)