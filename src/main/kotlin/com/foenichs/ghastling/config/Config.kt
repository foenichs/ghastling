package com.foenichs.ghastling.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val token: String
)