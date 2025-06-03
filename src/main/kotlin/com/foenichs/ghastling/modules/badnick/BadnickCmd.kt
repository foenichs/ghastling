package com.foenichs.ghastling.modules.badnick

import com.foenichs.ghastling.utils.entities.SlashCommandEvent
import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.minn.jda.ktx.interactions.components.Container
import dev.minn.jda.ktx.interactions.components.TextDisplay
import dev.minn.jda.ktx.messages.reply_
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.io.File

object BadnickCmd : SlashCommandEvent {
    override suspend fun trigger(it: SlashCommandInteractionEvent) {
        val user = it.getOption("user")?.asMember ?: return
        val nickname = user.nickname
        val displayName = user.effectiveName

        val characterMappings = loadCharacterMappings()
        val sourceName = nickname ?: displayName

        val updatedNickname = if (hasReplacableCharacter(sourceName, characterMappings)) {
            sanitizeString(sourceName, characterMappings)
        } else {
            sourceName
        }

        val finalNickname = if (nickname != null && updatedNickname == nickname) {
            "Nickname"
        } else {
            updatedNickname
        }

        user.modifyNickname(finalNickname).queue()

        it.reply_(
            useComponentsV2 = true,
            components = listOf(
                Container {
                    accentColor = 0xB6C8B5
                    +TextDisplay("The nickname of ${user.asMention} has been updated.")
                },
            ),
            ephemeral = true,
        ).queue()
    }

    private fun hasReplacableCharacter(text: String, mappings: Map<String, List<String>>): Boolean {
        var i = 0
        while (i < text.length) {
            val codePoint = text.codePointAt(i)
            val character = String(intArrayOf(codePoint), 0, 1)
            for ((_, fancyChars) in mappings) {
                if (fancyChars.contains(character)) {
                    return true
                }
            }
            i += Character.charCount(codePoint)
        }
        return false
    }

    private fun sanitizeString(text: String, mappings: Map<String, List<String>>): String {
        val sanitized = StringBuilder()
        var i = 0
        while (i < text.length) {
            val codePoint = text.codePointAt(i)
            val character = String(intArrayOf(codePoint), 0, 1)
            var replaced = false
            for ((normalChar, fancyChars) in mappings) {
                if (fancyChars.contains(character)) {
                    sanitized.append(normalChar)
                    replaced = true
                    break
                }
            }
            if (!replaced) sanitized.append(character)
            i += Character.charCount(codePoint)
        }
        return sanitized.toString()
    }

    private fun loadCharacterMappings(): Map<String, List<String>> {
        val file = File("src/main/kotlin/com/foenichs/ghastling/modules/badnick/confusable.json")
        val gson = Gson()
        val rootObject = gson.fromJson(file.readText(), JsonObject::class.java)
        val charactersArray = rootObject.getAsJsonArray("characters")

        val result = mutableMapOf<String, List<String>>()

        for (element in charactersArray) {
            val mapping = element.asJsonObject
            val keyStr = mapping.get("key").asString
            val valueStr = mapping.get("value").asString

            if (keyStr.length == 1) {
                val valueChars = mutableListOf<String>()
                var j = 0
                while (j < valueStr.length) {
                    val codePoint = valueStr.codePointAt(j)
                    valueChars.add(String(intArrayOf(codePoint), 0, 1))
                    j += Character.charCount(codePoint)
                }

                result[keyStr] = valueChars
            }
        }
        return result
    }
}