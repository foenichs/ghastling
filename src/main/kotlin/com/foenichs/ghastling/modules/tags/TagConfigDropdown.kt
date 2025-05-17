package com.foenichs.ghastling.modules.tags

import com.foenichs.ghastling.utils.entities.DropDownEvent
import dev.minn.jda.ktx.interactions.components.Container
import dev.minn.jda.ktx.interactions.components.TextDisplay
import dev.minn.jda.ktx.messages.editMessage_
import net.dv8tion.jda.api.components.selects.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent

object TagConfigDropdown : DropDownEvent {
    override suspend fun trigger(it: GenericSelectMenuInteractionEvent<String, StringSelectMenu>) {
        when (it.componentId) {
            "tagConfigRoles" -> {
                it.editMessage_(
                    useComponentsV2 = true,
                    components = listOf(Container {
                        accentColor = 0xB6C8B5
                        +TextDisplay("The selected roles are now able to send tags using Ghastling.")
                    }
                    ),
                ).queue()
            }
        }
    }
}