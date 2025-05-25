package com.foenichs.ghastling.modules.tags

import com.foenichs.ghastling.Ghastling
import com.foenichs.ghastling.utils.sql.SQL
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.components.Container
import dev.minn.jda.ktx.interactions.components.MediaGallery
import dev.minn.jda.ktx.interactions.components.TextDisplay
import dev.minn.jda.ktx.messages.MentionConfig
import dev.minn.jda.ktx.messages.Mentions
import dev.minn.jda.ktx.messages.send
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.concurrent.ConcurrentHashMap

object TagTriggering {
    // Per-channel, per-tag cooldowns: (channelId, tagName) -> timestamp when cooldown expires
    private val tagCooldowns = ConcurrentHashMap<Pair<Long, String>, Long>()
    private const val COOLDOWN_MILLIS = 10_000L // 10 seconds

    val onMessage = Ghastling.JDA.listener<MessageReceivedEvent> {
        val guildId = it.guild.idLong
        val channelId = it.channel.idLong
        val message = it.message
        val messageContent = message.contentRaw
        val channel = it.channel
        val member = it.member

        // If not in a guild context, ignore
        if (member == null) return@listener

        // Allowed roles check
        val allowedRolesResult = SQL.call("SELECT roleId FROM tagAllowedRoles WHERE guildId = ?") {
            setLong(1, guildId)
        }
        val allowedRoles = mutableSetOf<Long>()
        while (allowedRolesResult.next()) {
            allowedRoles.add(allowedRolesResult.getLong("roleId"))
        }
        if (allowedRoles.isNotEmpty()) {
            val memberRoleIds = member.roles.map { it.idLong }
            if (memberRoleIds.none { it in allowedRoles }) return@listener
        }

        val result = SQL.call("SELECT prefix FROM guildIndex WHERE guildId = ?") {
            setLong(1, guildId)
        }
        if (!result.next()) return@listener

        val prefix = result.getString("prefix")
        // Match prefix at start or after whitespace, then capture everything until the end of the line as tag name (including spaces)
        val regex = Regex("""(?<=^|\s)\Q$prefix\E\s*(.+)""")
        val match = regex.find(messageContent)
        val tagName = match?.groupValues?.get(1)?.trim()
        if (tagName.isNullOrBlank()) return@listener

        // PER-CHANNEL, PER-TAG COOLDOWN CHECK
        val now = System.currentTimeMillis()
        val cooldownKey = channelId to tagName.lowercase()
        val cooldownUntil = tagCooldowns[cooldownKey] ?: 0L
        if (now < cooldownUntil) {
            it.message.delete().queue()
            return@listener
        }
        tagCooldowns[cooldownKey] = now + COOLDOWN_MILLIS

        val tagResult =
            SQL.call("SELECT content, title, description, imageUrl, color FROM tags WHERE guildId = ? AND tagName = ?") {
                setLong(1, guildId)
                setString(2, tagName)
            }
        if (!tagResult.next()) return@listener

        val hasContainerContent = !tagResult.getString("title").isNullOrEmpty() || !tagResult.getString("description")
            .isNullOrEmpty() || !tagResult.getString("imageUrl").isNullOrEmpty()

        val content = tagResult.getString("content")
        if (content.isNullOrEmpty()) {
            // If "content" is null or empty, send the CV2 message
            channel.send(
                useComponentsV2 = true, components = listOfNotNull(if (hasContainerContent) Container {
                    accentColor = tagResult.getString("color")?.toIntOrNull(16)
                    tagResult.getString("title")?.let { +TextDisplay("### $it") }
                    tagResult.getString("description")?.let { +TextDisplay(it) }
                    tagResult.getString("imageUrl")?.let {
                        +MediaGallery { item(it) }
                    }
                } else null), mentions = Mentions(
                    MentionConfig.users(emptyList()),
                    MentionConfig.roles(emptyList()),
                    everyone = false,
                    here = false,
                )).queue()
        } else {
            // If "content" is NOT null or empty, send a non-CV2 message
            channel.send(
                useComponentsV2 = false, content = content, mentions = Mentions(
                    MentionConfig.users(emptyList()),
                    MentionConfig.roles(emptyList()),
                    everyone = false,
                    here = false,
                )
            ).queue()
        }

        // Only delete if the whole message is just the prefix and tag name (with optional whitespace)
        val deleteRegex = Regex("""^\s*\Q$prefix\E\s*.+\s*$""")
        if (deleteRegex.matches(messageContent)) {
            message.delete().queue()
        }
    }
}