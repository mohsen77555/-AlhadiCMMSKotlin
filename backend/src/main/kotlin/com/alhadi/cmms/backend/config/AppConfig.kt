package com.alhadi.cmms.backend.config

data class AppConfig(
    val host: String,
    val port: Int,
    val databaseUrl: String,
    val databaseUser: String,
    val databasePassword: String,
    val databasePoolSize: Int,
    val jwtSecret: String,
    val jwtIssuer: String,
    val jwtAudience: String,
    val jwtRealm: String,
    val accessTokenMinutes: Long,
    val refreshTokenDays: Long,
    val defaultOrganizationCode: String,
    val defaultOrganizationName: String,
    val bootstrapAdminName: String?,
    val bootstrapAdminUsername: String?,
    val bootstrapAdminPassword: String?,
    val corsHosts: List<String>,
    val environment: String
) {
    companion object {
        fun fromEnvironment(env: Map<String, String> = System.getenv()): AppConfig {
            fun value(name: String, default: String? = null): String =
                env[name]?.trim()?.takeIf { it.isNotEmpty() }
                    ?: default
                    ?: throw IllegalStateException("Missing required environment variable: $name")

            fun optional(name: String): String? = env[name]?.trim()?.takeIf { it.isNotEmpty() }

            val jwtSecret = value("JWT_SECRET")
            require(jwtSecret.length >= 32) { "JWT_SECRET must be at least 32 characters" }

            val bootstrapUsername = optional("BOOTSTRAP_ADMIN_USERNAME")
            val bootstrapPassword = optional("BOOTSTRAP_ADMIN_PASSWORD")
            if (bootstrapUsername != null || bootstrapPassword != null) {
                require(bootstrapUsername != null && bootstrapPassword != null) {
                    "BOOTSTRAP_ADMIN_USERNAME and BOOTSTRAP_ADMIN_PASSWORD must be provided together"
                }
                require(bootstrapPassword.length >= 12) {
                    "BOOTSTRAP_ADMIN_PASSWORD must be at least 12 characters"
                }
            }

            return AppConfig(
                host = value("HOST", "0.0.0.0"),
                port = value("PORT", "8080").toIntOrNull()
                    ?: throw IllegalArgumentException("PORT must be a number"),
                databaseUrl = value("DB_URL", "jdbc:postgresql://localhost:5432/alhadi_cmms"),
                databaseUser = value("DB_USER", "alhadi"),
                databasePassword = value("DB_PASSWORD"),
                databasePoolSize = value("DB_POOL_SIZE", "10").toIntOrNull()?.coerceIn(2, 50)
                    ?: throw IllegalArgumentException("DB_POOL_SIZE must be a number"),
                jwtSecret = jwtSecret,
                jwtIssuer = value("JWT_ISSUER", "alhadi-cmms-backend"),
                jwtAudience = value("JWT_AUDIENCE", "alhadi-cmms-app"),
                jwtRealm = value("JWT_REALM", "Alhadi CMMS API"),
                accessTokenMinutes = value("ACCESS_TOKEN_MINUTES", "15").toLongOrNull()?.coerceIn(5, 240)
                    ?: throw IllegalArgumentException("ACCESS_TOKEN_MINUTES must be a number"),
                refreshTokenDays = value("REFRESH_TOKEN_DAYS", "30").toLongOrNull()?.coerceIn(1, 180)
                    ?: throw IllegalArgumentException("REFRESH_TOKEN_DAYS must be a number"),
                defaultOrganizationCode = value("ORGANIZATION_CODE", "DEFAULT").uppercase(),
                defaultOrganizationName = value("ORGANIZATION_NAME", "Alhadi Maintenance"),
                bootstrapAdminName = optional("BOOTSTRAP_ADMIN_NAME") ?: "System Administrator",
                bootstrapAdminUsername = bootstrapUsername?.lowercase(),
                bootstrapAdminPassword = bootstrapPassword,
                corsHosts = value("CORS_HOSTS", "localhost:8080,localhost:3000")
                    .split(',')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() },
                environment = value("APP_ENV", "development").lowercase()
            )
        }
    }
}
