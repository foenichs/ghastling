package com.foenichs.ghastling.utils.sql
import com.foenichs.ghastling.config.DatabaseCredentials
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

object SQL {
    private val file = File("config/database.json")
    private val credentials = Json.decodeFromString<DatabaseCredentials>(file.readText())

    private var connection: Connection

    private fun connect(): Connection {
        val con = DriverManager.getConnection("jdbc:mariadb://${credentials.ip}:${credentials.port}/${credentials.database}", credentials.username, credentials.password)
        if (con.isValid(0))
            println("[MariaDB] Connection established to MariaDB")
        else println("[MariaDB] ERROR - MariaDB refused the connection")
        return con
    }

    suspend inline fun call(statement: String, arguments: PreparedStatement.() -> Unit): ResultSet =
        buildStatement(statement).apply(arguments).executeQuery()

    suspend fun buildStatement(statement: String): PreparedStatement {
        while (!connection.isValid(1)) {
            println("ERROR >> SQL - No valid connection!")
            connection = connect()
            delay(1000)
        }
        return connection.prepareStatement(statement)
    }

    init {
        connection = connect()
    }
}