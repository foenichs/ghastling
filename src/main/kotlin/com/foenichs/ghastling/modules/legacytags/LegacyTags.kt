package com.foenichs.ghastling.modules.legacytags

import com.foenichs.ghastling.Ghastling
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.components.Container
import dev.minn.jda.ktx.interactions.components.TextDisplay
import dev.minn.jda.ktx.messages.send
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object LegacyTags {
    val onMessage = Ghastling.JDA.listener<MessageReceivedEvent> {

        val message = it.message
        val channel = it.channel

        if (message.contentRaw.startsWith("!t m")) {
            message.delete().queue()
            channel.send(
                useComponentsV2 = true,
                components = listOf(
                    Container {
                        accentColor = 0xB6C8B5
                        +TextDisplay(
                            "### <:jeb_scream:1366752120384000050> Multiplayer requests are not allowed in this channel"
                        )
                        +TextDisplay(
                            "Read the https://discord.com/channels/1263854803553882123/1365368137909928006 and then use https://discord.com/channels/1263854803553882123/1365368172605345873 or https://discord.com/channels/1263854803553882123/1365368362041081926 to find friends to play with. You can also visit the official server list at <https://findmcserver.com/> to find servers to play on. Please do not post multiplayer requests in any other channels!\n" +
                                    "\n" +
                                    "**Note:** Posting in these channels requires the <@&1365368721488482465> role. Read the https://discord.com/channels/1263854803553882123/1365369051794243656 on how to get it. You can still read and DM people if you don't have the role."
                        )
                    },
                ),
            ).queue()
            return@listener
        }
        if (message.contentRaw.contains("!t m")) {
            channel.send(
                useComponentsV2 = true,
                components = listOf(
                    Container {
                        accentColor = 0xB6C8B5
                        +TextDisplay(
                            "### <:jeb_scream:1366752120384000050> Multiplayer requests are not allowed in this channel"
                        )
                        +TextDisplay(
                            "Read the https://discord.com/channels/1263854803553882123/1365368137909928006 and then use https://discord.com/channels/1263854803553882123/1365368172605345873 or https://discord.com/channels/1263854803553882123/1365368362041081926 to find friends to play with. You can also visit the official server list at <https://findmcserver.com/> to find servers to play on. Please do not post multiplayer requests in any other channels!\n" +
                                    "\n" +
                                    "**Note:** Posting in these channels requires the <@&1365368721488482465> role. Read the https://discord.com/channels/1263854803553882123/1365369051794243656 on how to get it. You can still read and DM people if you don't have the role."
                        )
                    },
                ),
            ).queue()
        }
    }
}