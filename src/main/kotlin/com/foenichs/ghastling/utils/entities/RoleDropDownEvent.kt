package com.foenichs.ghastling.utils.entities

import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent

interface RoleDropDownEvent {
    suspend fun trigger(it: EntitySelectInteractionEvent)
}