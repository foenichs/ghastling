package com.foenichs.ghastling.utils.manager

import com.foenichs.ghastling.modules.tags.TagConfigDropdown
import com.foenichs.ghastling.utils.entities.DropDownEvent
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.components.selects.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent

object DropDownManager {
    private val dropdowns = mapOf<String, DropDownEvent>(
        "tagConfigRoles" to TagConfigDropdown,
    )

    fun startListen(jda: JDA) = jda.listener<GenericSelectMenuInteractionEvent<String, StringSelectMenu>> {
        val id = it.componentId
        val commandClass = when {
            else -> dropdowns[id]
        }
        commandClass?.trigger(it)
    }
}