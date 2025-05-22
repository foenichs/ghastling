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

object TagTriggering {
    val onMessage = Ghastling.JDA.listener<MessageReceivedEvent> {
        val guildId = it.guild.idLong
        val message = it.message
        val messageContent = message.contentRaw
        val channel = it.channel
        val member = it.member

        // Allowed roles check
        val allowedRolesResult = SQL.call("SELECT roleId FROM tagAllowedRoles WHERE guildId = ?") {
            setLong(1, guildId)
        }
        val allowedRoles = mutableSetOf<Long>()
        while (allowedRolesResult.next()) {
            allowedRoles.add(allowedRolesResult.getLong("roleId"))
        }
        if (allowedRoles.isNotEmpty()) {
            val memberRoleIds = member?.roles?.map { it.idLong } ?: emptyList()
            val hasAllowedRole = memberRoleIds.any { it in allowedRoles }
            if (!hasAllowedRole) return@listener
        }

        val result = SQL.call("SELECT prefix FROM guildIndex WHERE guildId = ?") {
            setLong(1, guildId)
        }
        if (result.next()) {
            val prefix = result.getString("prefix")
            // Match prefix at start or after whitespace, then capture everything until the end of the line as tag name (including spaces)
            val regex = Regex("""(?<=^|\s)\Q$prefix\E\s*(.+)""")
            val match = regex.find(messageContent)
            val tagName = match?.groupValues?.get(1)?.trim()

            if (!tagName.isNullOrBlank()) {
                val tagResult =
                    SQL.call("SELECT content, title, description, imageUrl, color FROM tags WHERE guildId = ? AND tagName = ?") {
                        setLong(1, guildId)
                        setString(2, tagName)
                    }
                if (tagResult.next()) {
                    val hasContainerContent = !tagResult.getString("title").isNullOrEmpty() ||
                            !tagResult.getString("description").isNullOrEmpty() ||
                            !tagResult.getString("imageUrl").isNullOrEmpty()

                    channel.send(
                        useComponentsV2 = true,
                        components = listOfNotNull(
                            tagResult.getString("content")?.takeIf { it.isNotEmpty() }?.let { TextDisplay(it) },
                            if (hasContainerContent) Container {
                                accentColor = tagResult.getString("color")?.toIntOrNull(16)
                                tagResult.getString("title")?.let { +TextDisplay("### $it") }
                                tagResult.getString("description")?.let { +TextDisplay(it) }
                                tagResult.getString("imageUrl")?.let {
                                    +MediaGallery { item(it) }
                                }
                            } else null
                        ),
                        mentions = Mentions(
                            MentionConfig.users(emptyList()),
                            MentionConfig.roles(emptyList()),
                            everyone = false,
                            here = false,
                        )
                    ).queue()

                    // Only delete if the whole message is just the prefix and tag name (with optional whitespace)
                    val deleteRegex = Regex("""^\s*\Q$prefix\E\s*.+\s*$""")
                    if (deleteRegex.matches(messageContent)) {
                        message.delete().queue()
                    }
                }
            }
        }
    }
}