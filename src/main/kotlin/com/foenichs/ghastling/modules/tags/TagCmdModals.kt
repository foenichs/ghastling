package com.foenichs.ghastling.modules.tags

import com.foenichs.ghastling.utils.entities.ModalEvent
import com.foenichs.ghastling.utils.sql.SQL
import dev.minn.jda.ktx.interactions.components.Container
import dev.minn.jda.ktx.interactions.components.TextDisplay
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import java.net.URI

object TagCmdModals : ModalEvent {

    private fun validateTagValues(
        tagTitle: String?,
        tagDescription: String?,
        tagImageUrl: String?,
        tagColorInput: String?
    ): List<String> {
        val errors = mutableListOf<String>()

        if (tagTitle == null && tagDescription == null && tagImageUrl == null) {
            errors.add("You **must provide** at least a title, description, or image URL.")
        }

        if (!tagImageUrl.isNullOrBlank()) {
            try {
                URI(tagImageUrl).toURL()
            } catch (e: Exception) {
                errors.add("The URL of the image that has been provided is **not** valid.")
            }
        }

        if (!tagColorInput.isNullOrBlank()) {
            if (!(tagColorInput.matches(Regex("\\b[A-Fa-f0-9]{6}\\b")) ||
                        tagColorInput.matches(Regex("#\\b[A-Fa-f0-9]{6}\\b")))) {
                errors.add("The color that has been provided is **not** valid.")
            }
        }

        return errors
    }

    override suspend fun trigger(it: ModalInteractionEvent) {
        val baseModalId = it.modalId.split(":")[0]
        val tagName = it.modalId.split(":").getOrNull(1)

        val tagTitle = it.getValue("tagTitle")?.asString?.takeIf { s -> s.isNotBlank() }
        val tagDescription = it.getValue("tagDescription")?.asString?.takeIf { s -> s.isNotBlank() }
        val tagImageUrl = it.getValue("tagImageUrl")?.asString?.takeIf { s -> s.isNotBlank() }
        val tagColorInput = it.getValue("tagColor")?.asString?.takeIf { s -> s.isNotBlank() }

        // Strip the '#' from the color input if present, otherwise keep as is or null.
        val sanitizedTagColor = tagColorInput?.replace("#", "")

        val errors = when (baseModalId) {
            "tagAddModal" -> validateTagValues(
                tagTitle, tagDescription, tagImageUrl, tagColorInput
            )
            "tagEditModal" -> validateTagValues(
                tagTitle, tagDescription, tagImageUrl, tagColorInput
            )
            else -> emptyList()
        }

        if (errors.isNotEmpty()) {
            val errorMessage = if (errors.size > 1) {
                errors.joinToString("\n- ", prefix = "- ")
            } else {
                errors.first()
            }

            it.reply_(
                useComponentsV2 = true,
                components = listOf(
                    Container {
                        +TextDisplay(errorMessage)
                    },
                ),
                ephemeral = true,
            ).queue()
            return
        }

        when (baseModalId) {
            "tagAddModal" -> {
                SQL.call("INSERT INTO tags (guildId, tagName, title, description, imageUrl, color) VALUES (?, ?, ?, ?, ?, ?);") {
                    setLong(1, it.guild?.idLong ?: return)
                    setString(2, tagName)
                    setString(3, tagTitle)
                    setString(4, tagDescription)
                    setString(5, tagImageUrl)
                    setString(6, sanitizedTagColor)
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

            "tagEditModal" -> {
                SQL.call("UPDATE tags SET title = ?, description = ?, imageUrl = ?, color = ? WHERE guildId = ? AND tagName = ?;") {
                    setString(1, tagTitle)
                    setString(2, tagDescription)
                    setString(3, tagImageUrl)
                    setString(4, sanitizedTagColor)
                    setLong(5, it.guild?.idLong ?: return)
                    setString(6, tagName)
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
                            +TextDisplay("The **$tagName** tag was successfully edited, you can use it with `$prefix$tagName`.")
                        },
                    ),
                    ephemeral = true,
                ).queue()
            }

            "tagContentEditModal" -> {
                SQL.call("UPDATE tags SET content = ? WHERE guildId = ? AND tagName = ?;") {
                    setString(1, it.getValue("tagContent")?.asString ?: return)
                    setLong(2, it.guild?.idLong ?: return)
                    setString(3, tagName)
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
                            +TextDisplay("The **$tagName** tag was successfully edited, you can use it with `$prefix$tagName`.")
                        },
                    ),
                    ephemeral = true,
                ).queue()
            }
        }
    }
}