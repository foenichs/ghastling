package com.foenichs.ghastling.modules.tags

import java.util.concurrent.ConcurrentHashMap

object TagUserCooldown {
    private val cooldowns = ConcurrentHashMap<Pair<Long, String>, Long>() // (userId, tagName) -> timestamp
    fun userOnCooldown(userId: Long, tagName: String, cooldownMillis: Long): Boolean {
        val key = userId to tagName
        val now = System.currentTimeMillis()
        val expiresAt = cooldowns[key] ?: 0L
        return if (now < expiresAt) {
            true
        } else {
            cooldowns[key] = now + cooldownMillis
            false
        }
    }
}