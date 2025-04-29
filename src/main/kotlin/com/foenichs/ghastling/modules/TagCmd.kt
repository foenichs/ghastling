package com.foenichs.ghastling.modules

import com.foenichs.ghastling.utils.entities.SlashCommandEvent
import com.foenichs.ghastling.utils.sql.SQL
import dev.minn.jda.ktx.interactions.components.Container
import dev.minn.jda.ktx.interactions.components.TextDisplay
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

object TagCmd : SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        when (it.subcommandName) {
            "add" -> {
                val tagName = it.getOption("name")?.asString ?: return
                if (tagName.length > 32) {
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                accentColor = 0x9FAA9F
                                +TextDisplay("The tag name is too long, it has to be less than 32 characters.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                    return
                }

                val (channelId, messageId) = it.getOption("message")?.asString?.split("/")?.takeLast(2) ?: listOf(
                    null, null
                )
                val channel = channelId?.let { it1 -> it.guild?.getTextChannelById(it1) }
                val message = messageId?.let { it1 -> channel?.retrieveMessageById(it1)?.complete() } ?: return

                SQL.call("INSERT INTO tags (guild_id, tag_name, content, embeds, cv2, is_ephemeral, components) VALUES (?, ?, ?, ?, ?, ?, ?)") {
                    setLong(1, it.guild?.idLong ?: return)
                    setString(2, it.getOption("name")?.asString ?: return)
                    setString(3, message.contentRaw)
                    setString(4, message.embeds.toString())
                    setBoolean(5, message.isUsingComponentsV2)
                    setBoolean(6, message.isEphemeral)
                    setString(7, message.components.toString())
                }
                it.reply_(
                    useComponentsV2 = true,
                    components = listOf(
                        Container {
                            accentColor = 0xB6C8B5
                            +TextDisplay(
                                "The tag **$tagName** was successfully added, you can now use it with `?t $tagName`."
                            )
                        },
                    ),
                    ephemeral = true,
                ).queue()
            }

            "send" -> {
                val resultSet =
                    SQL.call("SELECT guild_id, tag_name, content, embeds, cv2, is_ephemeral, components FROM tags WHERE guild_id = ? AND tag_name = ?") {
                        setLong(1, it.guild?.idLong ?: return)
                        setString(2, it.getOption("name")?.asString ?: return)
                    }

                while (resultSet.next()) {
                    val guildId = resultSet.getLong("guild_id")
                    val tagName = resultSet.getString("tag_name")
                    val content = resultSet.getString("content")
                    val embeds = resultSet.getString("embeds")
                    val cv2 = resultSet.getBoolean("cv2")
                    val isEphemeral = resultSet.getBoolean("is_ephemeral")
                    val components = resultSet.getString("components")

//                    it.reply_(
//                        content = content,
// !!                     embeds = message.embeds.toCollection(mutableListOf()),
//                        useComponentsV2 = cv2,
//                        ephemeral = isEphemeral,
// !!                     components = message.components.toCollection(mutableListOf()),
//                    ).queue()
                }
            }
        }
    }
}