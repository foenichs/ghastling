package com.foenichs.ghastling.utils.manager

import com.foenichs.ghastling.Ghastling
import com.foenichs.ghastling.modules.tags.TagCmd
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.commands.Command
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.subcommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

object SlashCommandManager {
    private val commands = mapOf(
        "tag" to TagCmd
    )

    // Logs command executions in the console
    fun startListen(jda: JDA) = jda.listener<SlashCommandInteractionEvent> {
        val commandClass = commands[it.name] ?: return@listener
        val options = buildString { it.options.forEach { option -> append(option.asString + " ") } }
        println("[DcBot] ${it.user.name} executed /${it.name} ${it.subcommandName ?: ""} $options")
        commandClass.trigger(it)
    }

    init {

        // Adds commands to a test server
        val flame = Ghastling.JDA.getGuildById(1044265134253670400)
        flame?.updateCommands()?.addCommands()?.queue()

        // Adds global commands
        Ghastling.JDA.updateCommands().addCommands(Command("tag", "Manage tags for this server.") {
            defaultPermissions = DefaultMemberPermissions.enabledFor(
                Permission.MANAGE_SERVER
            )
            subcommand("add", "Adds a new tag for this server.") {
                option<String>(
                    "name", "The name to use and send this tag.", true
                ) { setMaxLength(32) }
            }
            subcommand("remove", "Removes a tag from this server.") {
                option<String>(
                    "name", "The name of the tag that will be removed.", true
                ) { setMaxLength(32) }
            }
            subcommand("edit", "Edits a existing tag on this server.") {
                option<String>(
                    "name", "The name of the tag that will be edited.", true
                ) { setMaxLength(32) }
            }
            subcommand("prefix", "Change the prefix to send tags.") {
                option<String>(
                    "prefix", "The prefix to send tags.", true
                ) { setMaxLength(16) }
                option<Boolean>(
                    "space", "Add a space behind the prefix.", true
                )
            }
            subcommand("permissions", "Changes which roles are allowed to send tags.")

//            subcommand("send", "Sends a tag of this server without any restrictions.") {
//                option<String>(
//                    "name", "The name of the tag you want to send.", true
//                )
//            }
        }).queue()
    }
}