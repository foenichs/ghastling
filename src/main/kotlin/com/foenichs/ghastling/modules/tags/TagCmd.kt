package com.foenichs.ghastling.modules.tags

import com.foenichs.ghastling.utils.entities.SlashCommandEvent
import com.foenichs.ghastling.utils.sql.SQL
import dev.minn.jda.ktx.interactions.components.*
import dev.minn.jda.ktx.messages.MentionConfig
import dev.minn.jda.ktx.messages.Mentions
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.components.selects.EntitySelectMenu

object TagCmd : SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        when (it.subcommandName) {
            "add" -> {
                val tagName = it.getOption("name")?.asString ?: return
                val existingTag = SQL.call("SELECT * FROM tags WHERE guildId = ? AND tagName = ?") {
                    setLong(1, it.guild?.idLong ?: return@call)
                    setString(2, tagName)
                }

                if (existingTag.next()) {
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                +TextDisplay("The tag **$tagName** already exists. You can edit it with </tag edit:1373059281884024974>.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                    return
                }

                if (tagName.length > 32) {
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                +TextDisplay("The tag name is **too long**, it has to be less than 32 characters.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                    return
                }
                if (!tagName.matches(Regex("^[a-zA-Z ]*\$"))) {
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                +TextDisplay("The tag name can't contain any **special characters**.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                    return
                }

                val tagContent = it.getOption("content")?.asString
                if (tagContent != null) {
                    val prefix = SQL.call("SELECT prefix FROM guildIndex WHERE guildId = ?") {
                        setLong(1, it.guild?.idLong ?: return)
                    }.use { resultSet ->
                        if (resultSet.next()) resultSet.getString("prefix") else null
                    }
                    SQL.call("INSERT INTO tags (guildId, tagName, content) VALUES (?, ?, ?);") {
                        setLong(1, it.guild?.idLong ?: return)
                        setString(2, tagName)
                        setString(3, tagContent)
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
                    return
                }

                it.replyModal(
                    Modal("tagAddModal:$tagName", "Add the $tagName tag") {
                        builder.addComponents(ActionRow {
                            +TextInput("tagTitle", "Title", TextInputStyle.SHORT) {
                                required = false
                                requiredLength = 0..256
                                placeholder = "Prominent text above the tag content."
                            }
                        }, ActionRow {
                            +TextInput("tagDescription", "Description", TextInputStyle.PARAGRAPH) {
                                required = false
                                placeholder =
                                    "Just add your tag content in this modal. You can use things like emojis or formatting."
                            }
                        }, ActionRow {
                            +TextInput("tagImageUrl", "Image URL", TextInputStyle.SHORT) {
                                required = false
                                placeholder = "https://example.com/image.png"
                            }
                        }, ActionRow {
                            +TextInput("tagColor", "Color (HEX)", TextInputStyle.SHORT) {
                                required = false
                                requiredLength = 0..7
                                placeholder = "#b6c8b5"
                            }
                        })
                    },
                ).queue()
            }

            "remove" -> {
                val tagName = it.getOption("name")?.asString ?: return
                val result = SQL.call("SELECT * FROM tags WHERE guildId = ? AND tagName = ?") {
                    setLong(1, it.guild?.idLong ?: return@call)
                    setString(2, tagName)
                }

                if (result.next()) {
                    SQL.call("DELETE FROM tags WHERE guildId = ? AND tagName = ?") {
                        setLong(1, it.guild?.idLong ?: return@call)
                        setString(2, tagName)
                    }
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                accentColor = 0xB6C8B5
                                +TextDisplay("The tag **$tagName** has been successfully removed.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                } else {
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                +TextDisplay("The tag **$tagName** does not exist. You can create it with </tag add:1373059281884024974>.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                }
            }

            "edit" -> {
                val tagName = it.getOption("name")?.asString ?: return
                val tagResult =
                    SQL.call("SELECT content, title, description, imageUrl, color FROM tags WHERE guildId = ? AND tagName = ?") {
                        setLong(1, it.guild?.idLong ?: return@call)
                        setString(2, tagName)
                    }

                if (!tagResult.next()) {
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                +TextDisplay("The tag **$tagName** does not exist. You can create it with </tag add:1373059281884024974>.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                    return
                }

                val tagContent = tagResult.getString("content")
                if (tagContent != null) {
                    it.replyModal(
                        Modal("tagContentEditModal:$tagName", "Edit the $tagName tag") {
                            builder.addComponents(ActionRow {
                                +TextInput("tagContent", "Content", TextInputStyle.PARAGRAPH) {
                                    required = false
                                    value = tagContent
                                }
                            })
                        },
                    ).queue()
                    return
                }

                val tagTitle = tagResult.getString("title")
                val tagDescription = tagResult.getString("description")
                val tagImageUrl = tagResult.getString("imageUrl")
                val tagColor = tagResult.getString("color")

                it.replyModal(
                    Modal("tagEditModal:$tagName", "Edit the $tagName tag") {
                        builder.addComponents(ActionRow {
                            +TextInput("tagTitle", "Title", TextInputStyle.SHORT) {
                                required = false
                                requiredLength = 0..256
                                placeholder = "Prominent text above the tag content."
                                value = tagTitle
                            }
                        }, ActionRow {
                            +TextInput("tagDescription", "Description", TextInputStyle.PARAGRAPH) {
                                required = false
                                placeholder =
                                    "Just add your tag content in this modal. You can use things like emojis or formatting."
                                value = tagDescription
                            }
                        }, ActionRow {
                            +TextInput("tagImageUrl", "Image URL", TextInputStyle.SHORT) {
                                required = false
                                placeholder = "https://example.com/image.png"
                                value = tagImageUrl
                            }
                        }, ActionRow {
                            +TextInput("tagColor", "Color (HEX)", TextInputStyle.SHORT) {
                                required = false
                                requiredLength = 0..7
                                placeholder = "#b6c8b5"
                                value = "#$tagColor"
                            }
                        })
                    },
                ).queue()
            }

            "list" -> {
                SQL.call("SELECT * FROM tags WHERE guildId = ?") {
                    setLong(1, it.guild?.idLong ?: return@call)
                }.use { resultSet ->
                    val tags = mutableListOf<String>()
                    while (resultSet.next()) {
                        tags.add(resultSet.getString("tagName"))
                    }
                    if (tags.isEmpty()) {
                        it.reply_(
                            useComponentsV2 = true,
                            components = listOf(
                                Container {
                                    accentColor = 0xB6C8B5
                                    +TextDisplay("This server does not have any tags yet. You can create one with </tag add:1373059281884024974>.")
                                },
                            ),
                            ephemeral = true,
                        ).queue()
                    } else {
                        it.reply_(
                            useComponentsV2 = true,
                            components = listOf(
                                Container {
                                    accentColor = 0xB6C8B5
                                    +TextDisplay("### Tags of ${it.guild?.name}\n`${tags.joinToString("`, `", postfix = "`")}")
                                },
                            ),
                            ephemeral = true,
                        ).queue()
                    }
                }
            }

            "prefix" -> {
                val prefixInput = it.getOption("prefix")?.asString ?: return
                val space = it.getOption("space")?.asBoolean ?: return
                val newPrefix = if (space) "$prefixInput " else prefixInput
                val existingPrefix = SQL.call("SELECT * FROM guildIndex WHERE guildId = ?") {
                    setLong(1, it.guild?.idLong ?: return@call)
                }

                if (existingPrefix.next() && existingPrefix.getString("prefix") == newPrefix) {
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                +TextDisplay("The prefix already is `$newPrefix`, nothing changed.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                    return
                } else {
                    SQL.call("UPDATE guildIndex SET prefix = ? WHERE guildId = ?") {
                        setString(1, newPrefix)
                        setLong(2, it.guild?.idLong ?: return@call)
                    }
                    it.reply_(
                        useComponentsV2 = true,
                        components = listOf(
                            Container {
                                accentColor = 0xB6C8B5
                                +TextDisplay("The prefix has been successfully changed to `$newPrefix`.")
                            },
                        ),
                        ephemeral = true,
                    ).queue()
                }
            }

            "permissions" -> {
                val result = SQL.call("SELECT * FROM tagAllowedRoles WHERE guildId = ?") {
                    setLong(1, it.guild?.idLong ?: return@call)
                }

                val roles = mutableListOf<Long>()
                while (result.next()) {
                    roles.add(result.getLong("roleId"))
                }

                val defaultValues = roles.map {
                    EntitySelectMenu.DefaultValue.role(it)
                }

                it.reply_(
                    useComponentsV2 = true, components = listOf(Container {
                        accentColor = 0xB6C8B5
                        +TextDisplay("### <:role:1374752116475822130> Allowed Roles\nHere, you can **add and remove roles that can send tags** using Ghastling.")
                    }, ActionRow {
                        +EntitySelectMenu(

                            placeholder = "No allowed roles, everyone can use tags.",
                            customId = "tagConfigRoles",
                            types = listOf(
                                EntitySelectMenu.SelectTarget.ROLE
                            ),
                            valueRange = 0..12,
                            builder = {
                                if (defaultValues.isNotEmpty()) {
                                    setDefaultValues(*defaultValues.toTypedArray())
                                }
                            })
                    }), ephemeral = true, mentions = Mentions(
                        MentionConfig.users(emptyList()),
                        MentionConfig.roles(emptyList()),
                        everyone = false,
                        here = false,
                    )
                ).queue()
            }

            "send" -> {
//                val resultSet =
//                    SQL.call("SELECT guild_id, tag_name, components FROM tags WHERE guild_id = ? AND tag_name = ?") {
//                        setLong(1, it.guild?.idLong ?: return)
//                        setString(2, it.getOption("name")?.asString ?: return)
//                    }
//
//                while (resultSet.next()) {
//                    val componentsJson = resultSet.getString("components")
//                    val components = kotlin.runCatching {
//                        json.decodeFromString<List<Component>>(componentsJson)
//                    }.getOrNull()?.map { it.toDiscord() }
//
//                    it.reply_(
//                        useComponentsV2 = true,
//                        components = components,
//                    ).queue()
//                }
            }
        }
    }
}