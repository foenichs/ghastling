package com.foenichs.ghastling.utils.entities

import net.dv8tion.jda.api.components.selects.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent

interface DropDownEvent {
    suspend fun trigger(it: GenericSelectMenuInteractionEvent<String, StringSelectMenu>)
}