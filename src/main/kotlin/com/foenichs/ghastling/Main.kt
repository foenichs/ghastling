package com.foenichs.ghastling

import com.foenichs.ghastling.config.Config
import com.foenichs.ghastling.modules.tags.TagCmdModals
import com.foenichs.ghastling.modules.tags.TagTriggering
import com.foenichs.ghastling.utility.ServerInitialisation
import com.foenichs.ghastling.utility.SystemInfoButton
import com.foenichs.ghastling.utils.manager.ButtonManager
import com.foenichs.ghastling.utils.manager.ModalManager
import com.foenichs.ghastling.utils.manager.RoleDropDownManager
import com.foenichs.ghastling.utils.manager.SlashCommandManager
import dev.minn.jda.ktx.jdabuilder.default
import dev.minn.jda.ktx.jdabuilder.intents
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant

fun main() {
    Ghastling
}

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}

val logger = LoggerFactory.getLogger("Ghastling")

object Ghastling {
    val JDA: JDA
    private val settings: Config

    init {
        val credentialFile = File("config/config.json")
        settings = Json.decodeFromString<Config>(credentialFile.readText())
        JDA = default(settings.token) {
            enableCache(CacheFlag.VOICE_STATE)
            setStatus(OnlineStatus.ONLINE)
            intents += listOf(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.MESSAGE_CONTENT,
            )
        }

        JDA.awaitReady()
        SystemInfoButton.startTime = Instant.now()

        ButtonManager.startListen(JDA)
        RoleDropDownManager.startListen(JDA)
        ModalManager.startListen(JDA)
        SlashCommandManager.startListen(JDA)

        ServerInitialisation
        TagCmdModals
        TagTriggering

        println("[Ghastling] The application started successfully!")
    }
}