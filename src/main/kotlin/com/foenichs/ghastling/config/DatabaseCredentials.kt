package com.foenichs.ghastling.config

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseCredentials(
    val ip: String,
    val port: Int,
    val username: String,
    val password: String,
    val database: String
)