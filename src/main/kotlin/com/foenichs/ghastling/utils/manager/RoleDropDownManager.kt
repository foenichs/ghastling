package com.foenichs.ghastling.utils.manager

import com.foenichs.ghastling.modules.tags.TagConfigDropdown
import com.foenichs.ghastling.utils.entities.RoleDropDownEvent
import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent

object RoleDropDownManager {
    private val dropdowns = mapOf<String, RoleDropDownEvent>(
        "tagConfigRoles" to TagConfigDropdown,
    )

    fun startListen(jda: JDA) = jda.listener<EntitySelectInteractionEvent> {
        val id = it.componentId
        val commandClass = when {
            else -> dropdowns[id]
        }
        commandClass?.trigger(it)
    }
}