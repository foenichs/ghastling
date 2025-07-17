package com.foenichs.ghastling.utility

import com.foenichs.ghastling.utils.entities.SlashCommandEvent
import com.foenichs.ghastling.utils.sql.SQL
import dev.minn.jda.ktx.interactions.components.*
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.components.separator.Separator.Spacing
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

object HelpCmd : SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        val guildId = it.guild?.idLong ?: return
        val result = SQL.call("SELECT prefix FROM guildIndex WHERE guildId = ?") {
            setLong(1, guildId)
        }
        val prefix = if (result.next()) {
            result.getString("prefix")
        } else {
            "!"
        }
        it.reply_(
            useComponentsV2 = true, components = listOf(Container {
                accentColor = 0xB6C8B5
//                    +MediaGallery {
//                        item("https://i.imgur.com/TZw9gNu.png")
//                    }
                +TextDisplay(
                    "-# <:ghastling:1367898099887902772> Ghastling"
                )
                +TextDisplay(
                    "-# A cute little tag/faq bot that you can easily configure and use on your own server."
                )
                +Separator(true, spacing = Spacing.SMALL)
                +TextDisplay(
                    "**Tags** let you send long or important messages quickly, which is great for busy servers. Use the </tag:1373059281884024974> command to create, edit, remove, or list tags. You can also restrict tag usage to specific roles. Each tag has a 10-second cooldown per channel."
                )
                +Separator(false, spacing = Spacing.SMALL)
                +TextDisplay(
                    "Using </tag add:1373059281884024974> will open a menu to configure the embed sent when the tag is used. Alternatively, you can add content directly in the command option to send it as a regular message instead of an embed."
                )
                +Separator(false, spacing = Spacing.SMALL)
                +TextDisplay(
                    "The **prefix** of the server changes how members use tags. You can change it using </tag prefix:1373059281884024974>. Your current prefix is `$prefix`, so typing `${prefix}rules` will send the `rules` tag. Tag commands are deleted after the tag is sent, if the message doesn't contain other content. The tag command doesn't have to be at the start of the message."
                )
                +Separator(true, spacing = Spacing.SMALL)
                +TextDisplay(
                    "-# Feel free to report bugs or suggest features by opening an issue on GitHub."
                )
            }, ActionRow {
                +secondary("SystemInfo", "System Info")
                +link("https://github.com/foenichs/ghastling", "GitHub")
            }), ephemeral = true
        ).queue()
    }
}