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

        // Adds commands to a private server
        val pokestop = Ghastling.JDA.getGuildById(1263854803553882123)
        pokestop?.updateCommands()?.addCommands(
            Command("tag", "Manage tags for this server.") {
                defaultPermissions = DefaultMemberPermissions.enabledFor(
                    Permission.MESSAGE_MANAGE
                )
                subcommand("add", "Adds a new tag for this server.") {
                    option<String>(
                        "name", "The name to use and send this tag.", true
                    )
                    option<String>(
                        "message", "The url of the message that should be sent.", true
                    )
                }
                subcommand("send", "Sends a tag of this server.") {
                    option<String>(
                        "name", "The name of the tag you want to send.", true
                    )
                }
            })?.queue()

        // Adds global commands
        Ghastling.JDA.updateCommands().addCommands(
//            Insert slash command here
        ).queue()
    }
}