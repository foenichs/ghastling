package com.foenichs.ghastling.modules.tags

import com.foenichs.ghastling.utils.entities.RoleDropDownEvent
import com.foenichs.ghastling.utils.sql.SQL
import dev.minn.jda.ktx.interactions.components.Container
import dev.minn.jda.ktx.interactions.components.TextDisplay
import dev.minn.jda.ktx.messages.editMessage_
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent

object TagConfigDropdown : RoleDropDownEvent {
    override suspend fun trigger(it: EntitySelectInteractionEvent) {
        when (it.componentId) {
            "tagConfigRoles" -> {
                val roles = it.values
                val roleIds = roles.map { role -> role.idLong }

                roles.forEach { role ->
                    SQL.call("INSERT INTO tagAllowedRoles (guildId, roleId) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM tagAllowedRoles WHERE guildId = ? AND roleId = ?)") {
                        setLong(1, it.guild?.idLong ?: return@call)
                        setLong(2, role.idLong)
                        setLong(3, it.guild?.idLong ?: return@call)
                        setLong(4, role.idLong)
                    }
                }

                if (roleIds.isEmpty()) {
                    SQL.call("DELETE FROM tagAllowedRoles WHERE guildId = ?") {
                        setLong(1, it.guild?.idLong ?: return@call)
                    }
                } else {
                    SQL.call("DELETE FROM tagAllowedRoles WHERE guildId = ? AND roleId NOT IN (${roleIds.joinToString(",")})") {
                        setLong(1, it.guild?.idLong ?: return@call)
                    }
                }

                val rolesString = if (roles.isEmpty()) {
                    "There are no allowed roles, everyone can use tags now."
                } else {
                    "The allowed roles have been successfully updated."
                }
                it.editMessage_(
                    useComponentsV2 = true,
                    components = listOf(
                        Container {
                            accentColor = 0xB6C8B5
                            +TextDisplay(rolesString)
                        },
                    ),
                ).queue()
            }
        }
    }
}