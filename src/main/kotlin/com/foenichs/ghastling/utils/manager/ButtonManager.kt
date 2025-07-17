package com.foenichs.ghastling.utils.manager

import com.foenichs.ghastling.utility.SystemInfoButton
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

object ButtonManager {
    private val buttons = mapOf(
        "SystemInfo" to SystemInfoButton
    )

    fun startListen(jda: JDA) = jda.listener<ButtonInteractionEvent> {
        val id = it.button.id ?: return@listener
        val commandClass = buttons[id]
        commandClass?.trigger(it)
    }
}