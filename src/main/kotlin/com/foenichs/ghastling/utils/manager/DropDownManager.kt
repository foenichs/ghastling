package com.foenichs.ghastling.utils.manager

import com.foenichs.ghastling.utils.entities.DropDownEvent
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

object DropDownManager {
    private val dropdowns = mapOf<String, DropDownEvent>(
    )

    fun startListen(jda: JDA) = jda.listener<GenericSelectMenuInteractionEvent<String, StringSelectMenu>> {}
}