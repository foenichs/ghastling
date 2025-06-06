package com.foenichs.ghastling.utils.manager

import com.foenichs.ghastling.modules.badnick.BadnickCmd
import com.foenichs.ghastling.Ghastling
import com.foenichs.ghastling.modules.tags.TagCmd
import com.foenichs.ghastling.utility.HelpCmd
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.commands.Command
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.subcommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

object SlashCommandManager {
    private val commands = mapOf(
        "tag" to TagCmd,
        "help" to HelpCmd,
        "badnick" to BadnickCmd
    )

    // Logs command executions in the console
    fun startListen(jda: JDA) = jda.listener<SlashCommandInteractionEvent> {
        val commandClass = commands[it.name] ?: return@listener
        val options = buildString { it.options.forEach { option -> append(option.asString + " ") } }
        println("[Ghastling] ${it.user.name} executed /${it.name} ${it.subcommandName ?: ""} $options")
        commandClass.trigger(it)
    }

    init {

        // Adds commands to a test server
        val flame = Ghastling.JDA.getGuildById(1044265134253670400)
        flame?.updateCommands()?.addCommands(
            Command("badnick", "Removes special characters and cleans up nicknames (Experimental)") {
                option<User>("user", "The user whose nickname should be cleaned up.", true)
                defaultPermissions = DefaultMemberPermissions.enabledFor(
                    Permission.NICKNAME_MANAGE
                )
            }
        )?.queue()

        // Adds global commands
        Ghastling.JDA.updateCommands().addCommands(Command("tag", "Manage tags for this server.") {
            defaultPermissions = DefaultMemberPermissions.enabledFor(
                Permission.MANAGE_SERVER
            )
            subcommand("add", "Adds a new tag for this server.") {
                option<String>(
                    "name", "The name to use and send this tag.", true
                ) { setMaxLength(32) }
                option<String>(
                    "content", "Optional content, if you don't want to use an embed.", false
                ) { setMaxLength(4000) }
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
            subcommand("list", "Shows all tags of this server.")
            subcommand("prefix", "Change the prefix to send tags.") {
                option<String>(
                    "prefix", "The prefix to send tags.", true
                ) { setMaxLength(16) }
                option<Boolean>(
                    "space", "Add a space behind the prefix.", true
                )
            }
            subcommand("permissions", "Changes which roles are allowed to send tags.")
            subcommand("import", "Import tags from another server (Experimental)") {
                option<String>(
                    "guild", "The ID of the server you want to import tags from.", true
                )
            }
            subcommand("preview", "Lets you preview a tag without sending it.") {
                option<String>(
                    "name", "The name of the tag you want to preview.", true
                )
            }
        }, Command("help", "Get information about Ghastling and how to use it.") {
            defaultPermissions = DefaultMemberPermissions.enabledFor(
                Permission.MANAGE_SERVER
            )
        }).queue()
    }
}