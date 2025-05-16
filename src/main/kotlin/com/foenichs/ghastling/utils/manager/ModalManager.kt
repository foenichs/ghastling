package com.foenichs.ghastling.utils.manager

import com.foenichs.ghastling.modules.tags.TagCmdModals
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

object ModalManager {
    private val modals = mapOf<String, com.foenichs.ghastling.utils.entities.ModalEvent>(
        "tagAddModal" to TagCmdModals,
        "tagEditModal" to TagCmdModals
    )

    fun startListen(jda: JDA) = jda.listener<ModalInteractionEvent> {
        // Handling both formats - with and without parameters
        val baseModalId = if (it.modalId.contains(":")) {
            it.modalId.split(":")[0]
        } else {
            it.modalId
        }
        val commandClass = modals[baseModalId]
        if (commandClass == null) {
            println("No command class found for modal ID: $baseModalId")
        } else {
            commandClass.trigger(it)
        }
    }
}