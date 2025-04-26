package com.foenichs.ghastling.utils.manager

import com.foenichs.ghastling.utils.entities.ModalEvent
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

object ModalManager {
    private val modals = mapOf<String, com.foenichs.ghastling.utils.entities.ModalEvent>(
//        "example" to ExampleEvent
    )

    fun startListen(jda: JDA) = jda.listener<ModalInteractionEvent> {
        val id = it.modalId
        val commandClass = modals[id]
        commandClass?.trigger(it)
    }
}