package com.foenichs.ghastling.utility

import com.foenichs.ghastling.utils.entities.ButtonEvent
import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.JDAInfo
import java.time.Instant

object SystemInfoButton : ButtonEvent {

    lateinit var startTime: Instant

    override suspend fun trigger(it: ButtonInteractionEvent) {
        val epochSeconds = if (this::startTime.isInitialized) startTime.epochSecond else 0L
        val relativeTimestamp = if (epochSeconds > 0) "<t:$epochSeconds:R>" else "Unknown"

        // Gateway ping (WebSocket latency)
        val gatewayPing = it.jda.gatewayPing

        // REST ping (true bot ping)
        val restPing = withContext(Dispatchers.IO) { it.jda.restPing.await() }

        // JDA Version
        val jdaVersion = JDAInfo.VERSION

        // Try to extract the commit hash from the version string, e.g. 5.0.0-beta.24_e24872e
        val commitRegex = Regex("_(\\w{7,})$")
        val commitHash = commitRegex.find(jdaVersion)?.groupValues?.getOrNull(1)

        val jdaVersionField = if (commitHash != null) {
            "[`${jdaVersion}`](https://github.com/discord-jda/JDA/commit/$commitHash)"
        } else {
            "`${jdaVersion}`"
        }

        it.deferReply(true).queue()
        it.hook.send(
            embeds = listOf(
                Embed {
                    title = "<:ghastling:1367898099887902772> System Info"
                    color = 0xB6C8B5
                    field {
                        name = "Bot Uptime"
                        value = relativeTimestamp
                        inline = true
                    }
                    field {
                        name = "JDA Version"
                        value = jdaVersionField
                        inline = true
                    }
                    field {
                        name = "Rest Ping"
                        value = "`${restPing} ms`"
                        inline = true
                    }
                    field {
                        name = "Gateway Ping"
                        value = "`${gatewayPing} ms`"
                        inline = true
                    }
                }),
        ).queue()
    }
}