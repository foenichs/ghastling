package com.foenichs.ghastling.utility

import com.foenichs.ghastling.Ghastling.JDA
import com.foenichs.ghastling.utils.sql.SQL
import dev.minn.jda.ktx.events.listener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent

object ServerInitialisation {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            val dbGuilds = mutableSetOf<Long>()
            SQL.call("SELECT guildId FROM guildIndex") {}.use { resultSet ->
                while (resultSet.next()) {
                    dbGuilds.add(resultSet.getLong("guildId"))
                }
            }
            val activeGuilds = JDA.guilds.map { it.idLong }.toSet()
            for (guildId in dbGuilds) {
                if (guildId !in activeGuilds) {
                    SQL.call("DELETE FROM guildIndex WHERE guildId = ?") {
                        setLong(1, guildId)
                    }
                }
            }
            for (guildId in activeGuilds) {
                val guild = JDA.getGuildById(guildId) ?: return@launch
                val guildName = guild.name
                val memberCount = guild.memberCount

                if (guildId !in dbGuilds) {
                    SQL.call("INSERT INTO guildIndex (guildId, guildName, memberCount) VALUES (?, ?, ?)") {
                        setLong(1, guild.idLong)
                        setString(2, guildName)
                        setInt(3, memberCount)
                    }
                }
                else {
                    SQL.call("UPDATE guildIndex SET guildName = ?, memberCount = ? WHERE guildId = ?") {
                        setString(1, guildName)
                        setInt(2, memberCount)
                        setLong(3, guild.idLong)
                    }
                }
            }
        }
    }

    val onJoin = JDA.listener<GuildJoinEvent> {
        val guild = it.guild
        SQL.call("SELECT COUNT(*) FROM guildIndex WHERE guildId = ?") {
            setLong(1, guild.idLong)
        }.use { resultSet ->
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                SQL.call("INSERT INTO guildIndex (guildId, guildName, memberCount) VALUES (?, ?, ?)") {
                    setLong(1, guild.idLong)
                    setString(2, guild.name)
                    setInt(3, guild.memberCount)
                }
            }
        }
    }
    val onLeave = JDA.listener<GuildLeaveEvent> {
        val guild = it.guild
        SQL.call("SELECT COUNT(*) FROM guildIndex WHERE guildId = ?") {
            setLong(1, guild.idLong)
        }.use { resultSet ->
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                SQL.call("DELETE FROM guildIndex WHERE guildId = ?") {
                    setLong(1, guild.idLong)
                }
            }
        }
    }
}