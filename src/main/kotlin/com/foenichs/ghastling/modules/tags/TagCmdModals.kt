package com.foenichs.ghastling.modules.tags

import com.foenichs.ghastling.utils.entities.ModalEvent
import com.foenichs.ghastling.utils.sql.SQL
import dev.minn.jda.ktx.interactions.components.Container
import dev.minn.jda.ktx.interactions.components.TextDisplay
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import java.net.URL

object TagCmdModals : ModalEvent {
    override suspend fun trigger(it: ModalInteractionEvent) {
        val baseModalId = it.modalId.split(":")[0]
        when (baseModalId) {
            "tagAddModal" -> {
                val tagName = it.modalId.split(":").getOrNull(1)

                val tagTitle = it.getValue("tagTitle")?.asString?.takeIf { it.isNotBlank() }
                val tagDescription = it.getValue("tagDescription")?.asString?.takeIf { it.isNotBlank() }
                val tagImageUrl = it.getValue("tagImageUrl")?.asString?.takeIf { it.isNotBlank() }
                val tagColor = it.getValue("tagColor")?.asString

                if (tagTitle == null && tagDescription == null && tagImageUrl == null) {
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                +TextDisplay("You **must provide** at least a title, description, or image URL.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                    return
                }

                if (tagImageUrl != null) {
                    try {
                        URL(tagImageUrl) // This will throw an exception if the URL is invalid
                    } catch (e: Exception) {
                        it.reply_(
                            useComponentsV2 = true,
                            components = listOf(
                                Container {
                                    +TextDisplay("The URL of the image that has been provided is **not** valid.")
                                },
                            ),
                            ephemeral = true,
                        ).queue()
                        return
                    }
                }

                SQL.call("INSERT INTO tags (guildId, tagName, title, description, imageUrl, color) VALUES (?, ?, ?, ?, ?, ?);") {
                    setLong(1, it.guild?.idLong ?: return)
                    setString(2, tagName)
                    setString(3, tagTitle)
                    setString(4, tagDescription)
                    setString(5, tagImageUrl)
                    setString(6, tagColor)
                }

                val prefix = SQL.call("SELECT prefix FROM guildIndex WHERE guildId = ?") {
                    setLong(1, it.guild?.idLong ?: return)
                }.use { resultSet ->
                    if (resultSet.next()) resultSet.getString("prefix") else null
                }

                it.reply_(
                    useComponentsV2 = true,
                    components = listOf(
                        Container {
                            accentColor = 0xB6C8B5
                            +TextDisplay("The **$tagName** tag was successfully added, you can now use it with `$prefix$tagName`.")
                        },
                    ),
                    ephemeral = true,
                ).queue()
            }
        }
    }
}