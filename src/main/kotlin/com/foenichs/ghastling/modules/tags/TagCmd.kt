package com.foenichs.ghastling.modules.tags

import com.foenichs.ghastling.utils.entities.SlashCommandEvent
import dev.minn.jda.ktx.interactions.components.*
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.components.textinput.TextInputStyle
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


                it.replyModal(
                    Modal("tagAddModal:$tagName", "Add the $tagName tag") {
                        builder.addComponents(
                            ActionRow {
                                +TextInput("tagTitle", "Title", TextInputStyle.SHORT) {
                                    required = false
                                    requiredLength = 0..256
                                    placeholder = "How to use Ghastling"
                                }
                            },
                            ActionRow {
                                +TextInput("tagDescription", "Description", TextInputStyle.PARAGRAPH) {
                                    required = false
                                    placeholder = "Just add your tag content in this modal. You can use things like emojis or formatting."
                                }
                            },
                            ActionRow {
                                +TextInput("tagImageUrl", "Image URL", TextInputStyle.SHORT) {
                                    required = false
                                    placeholder = "https://example.com/image.png"
                                }
                            },
                            ActionRow {
                                +TextInput("tagColor", "Color (HEX)", TextInputStyle.SHORT) {
                                    required = false
                                    requiredLength = 0..7
                                    placeholder = "#B6C8B5"
                                }
                            }
                        )
                    },
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