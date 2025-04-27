package com.foenichs.ghastling.modules

import com.foenichs.ghastling.utils.entities.SlashCommandEvent
import dev.minn.jda.ktx.interactions.components.Modal
import dev.minn.jda.ktx.interactions.components.TextInput
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

object TagCmd : SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        when (it.subcommandName) {
            "add" -> {
                val tagname = it.getOption("name")?.asString
                if (tagname?.length!! > 32) {
                    it.reply_(embeds = listOf(Embed {
                        description = "This tag name is too long. It must be less than **32** characters."
                    }), ephemeral = true).queue()
                    return
                }

                it.replyModal(
                    Modal("tagAddModal:$tagname", "Add the \"$tagname\" tag") {
                        builder.addActionRow(
                            TextInput(
                                id = "tagContent",
                                label = "Message Content",
                                style = TextInputStyle.PARAGRAPH,
                                required = false,
                                placeholder = "The message content that will be sent."
                            )
                        )
                        builder.addActionRow(
                            TextInput(
                                "tagTitle",
                                "Title",
                                TextInputStyle.SHORT,
                                required = false,
                                requiredLength = 1..256,
                                placeholder = "The title of the embed that will be sent."
                            )
                        )
                        builder.addActionRow(
                            TextInput(
                                "tagDescription",
                                "Description",
                                TextInputStyle.PARAGRAPH,
                                required = false,
                                placeholder = "The description of the embed that will be sent.",
                            )
                        )
                        builder.addActionRow(
                            TextInput(
                                "tagColor",
                                "Accent Color",
                                TextInputStyle.SHORT,
                                required = false,
                                placeholder = "#B6C8B5",
                                requiredLength = 6..7,
                            )
                        )
                    }
                ).queue()
            }
        }
    }
}