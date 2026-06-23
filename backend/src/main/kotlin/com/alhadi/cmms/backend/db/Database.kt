package com.alhadi.cmms.backend.db

import com.alhadi.cmms.backend.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

class Database(config: AppConfig) : AutoCloseable {
    private val dataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = config.databaseUrl
            username = config.databaseUser
            password = config.databasePassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = config.databasePoolSize
            minimumIdle = 1
            connectionTimeout = 10_000
            validationTimeout = 5_000
            idleTimeout = 300_000
            maxLifetime = 1_500_000
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            poolName = "alhadi-backend-pool"
            addDataSourceProperty("ApplicationName", "alhadi-cmms-backend")
            addDataSourceProperty("tcpKeepAlive", "true")
        }
    )

    init {
        migrate()
    }

    fun <T> query(block: (Connection) -> T): T = dataSource.connection.use(block)

    fun <T> transaction(block: (Connection) -> T): T = dataSource.connection.use { connection ->
        val originalAutoCommit = connection.autoCommit
        connection.autoCommit = false
        try {
            val result = block(connection)
            connection.commit()
            result
        } catch (error: Throwable) {
            connection.rollback()
            throw error
        } finally {
            connection.autoCommit = originalAutoCommit
        }
    }

    fun ping(): Boolean = runCatching {
        query { connection ->
            connection.prepareStatement("SELECT 1").use { statement ->
                statement.executeQuery().use { result -> result.next() && result.getInt(1) == 1 }
            }
        }
    }.getOrDefault(false)

    private fun migrate() {
        transaction { connection ->
            connection.createStatement().use { statement ->
                statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS schema_migrations (
                        version INTEGER PRIMARY KEY,
                        name VARCHAR(200) NOT NULL,
                        applied_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
                    )
                    """.trimIndent()
                )
            }

            val migrations = listOf(
                Migration(1, "baseline", "db/migration/V1__baseline.sql")
            )

            migrations.forEach { migration ->
                val applied = connection.prepareStatement(
                    "SELECT EXISTS(SELECT 1 FROM schema_migrations WHERE version = ?)"
                ).use { statement ->
                    statement.setInt(1, migration.version)
                    statement.executeQuery().use { result -> result.next() && result.getBoolean(1) }
                }
                if (!applied) {
                    val script = javaClass.classLoader.getResourceAsStream(migration.resource)
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: throw IllegalStateException("Missing migration resource: ${migration.resource}")
                    splitStatements(script).forEach { sql ->
                        connection.createStatement().use { statement -> statement.execute(sql) }
                    }
                    connection.prepareStatement(
                        "INSERT INTO schema_migrations(version, name) VALUES (?, ?)"
                    ).use { statement ->
                        statement.setInt(1, migration.version)
                        statement.setString(2, migration.name)
                        statement.executeUpdate()
                    }
                }
            }
        }
    }

    private fun splitStatements(script: String): List<String> {
        val statements = mutableListOf<String>()
        val current = StringBuilder()
        var inSingleQuote = false
        var index = 0
        while (index < script.length) {
            val char = script[index]
            if (char == '\'' && (index == 0 || script[index - 1] != '\\')) {
                if (inSingleQuote && index + 1 < script.length && script[index + 1] == '\'') {
                    current.append("''")
                    index += 2
                    continue
                }
                inSingleQuote = !inSingleQuote
            }
            if (char == ';' && !inSingleQuote) {
                current.toString().trim().takeIf { it.isNotEmpty() }?.let(statements::add)
                current.clear()
            } else {
                current.append(char)
            }
            index++
        }
        current.toString().trim().takeIf { it.isNotEmpty() }?.let(statements::add)
        return statements
    }

    override fun close() {
        dataSource.close()
    }

    private data class Migration(val version: Int, val name: String, val resource: String)
}
