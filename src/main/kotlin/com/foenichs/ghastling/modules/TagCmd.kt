package com.foenichs.ghastling.modules

import com.foenichs.ghastling.utils.entities.SlashCommandEvent
import dev.minn.jda.ktx.interactions.components.Modal
import dev.minn.jda.ktx.interactions.components.TextInput
import dev.minn.jda.ktx.interactions.components.replyModal
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import java.awt.Color

object TagCmd : SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        when (it.subcommandName) {
            "add" -> {
                val name = it.getOption("name")?.asString
                if (name?.length!! > 32) {
                    it.reply_(embeds = listOf(Embed {
                        description = "This tag name is too long. It must be less than **32** characters."
                    }), ephemeral = true).queue()
                    return
                }
                it.replyModal(
                    Modal("addTagModule", "Add a new tag") {
                        builder.addActionRow(
                            TextInput(
                                "tagTitle",
                                "Title",
                                TextInputStyle.SHORT,
                                true,
                                requiredLength = 1..256,
                                placeholder = "The title of the embed that is sent."
                            )
                        )
                        builder.addActionRow(
                            TextInput(
                                "tagContent",
                                "Description",
                                TextInputStyle.PARAGRAPH,
                                true,
                                placeholder = "The description of the embed that is sent.",
                            )
                        )
                        builder.addActionRow(
                            TextInput(
                                "tagColor",
                                "Accent Color",
                                TextInputStyle.SHORT,
                                false,
                                placeholder = "#B6C8B5",
                                requiredLength = 6..7,
                            )
                        )
                    }
                ).queue()
                it.reply_(embeds = listOf(Embed {
                    description = "The tag **$name** was successfully added."
                    color = 0xB6C8B5
                }), ephemeral = true).queue()
            }
        }
    }
}