package com.foenichs.ghastling.modules

import com.foenichs.ghastling.utils.entities.ModalEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

object TagAddModal : ModalEvent {
    override suspend fun trigger(it: ModalInteractionEvent) {
        val guild = it.guild
        val tagContent = it.getValue("tagTitle")?.asString
        val tagDescription = it.getValue("tagDescription")?.asString
        val tagName = it.getValue("tagColor")?.asString
    }
}