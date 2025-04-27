package com.foenichs.ghastling.modules

import com.foenichs.ghastling.utils.entities.ModalEvent
import dev.minn.jda.ktx.interactions.components.Container
import dev.minn.jda.ktx.interactions.components.TextDisplay
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

object TagCmdModals : ModalEvent {
    override suspend fun trigger(it: ModalInteractionEvent) {
        val baseModalId = it.modalId.split(":")[0]
        when (baseModalId) {
            "tagAddModal" -> {
                val tagname = it.modalId.split(":").getOrNull(1)
                it.reply_(
                    useComponentsV2 = true,
                    components = listOf(
                        Container {
                            accentColor = 0xB6C8B5
                            uniqueId = 1
                            +TextDisplay("The **$tagname** tag was successfully added, you can now use it with `?t $tagname`.")
                        },
                    ),
                    ephemeral = true,
                ).queue()
                val tagContent = it.getValue("tagContent")?.asString
                val tagTitle = it.getValue("tagTitle")?.asString
                val tagDescription = it.getValue("tagDescription")?.asString
                val tagColor = it.getValue("tagColor")?.asString?.toIntOrNull() ?: 0xB6C8B5
            }
        }
    }
}