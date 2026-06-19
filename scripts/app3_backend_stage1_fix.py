from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def replace_once(path: str, old: str, new: str, label: str) -> None:
    file = ROOT / path
    text = file.read_text(encoding="utf-8")
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected one match, found {count}")
    file.write_text(text.replace(old, new, 1), encoding="utf-8")


# JWT service is an implementation detail and may accept the internal UserRecord model.
replace_once(
    "backend/src/main/kotlin/com/alhadi/cmms/backend/security/Security.kt",
    "class JwtService(private val config: AppConfig) {",
    "internal class JwtService(private val config: AppConfig) {",
    "JwtService visibility",
)

# JWTPrincipal payload implements Payload rather than DecodedJWT in Ktor 3.5.
application_path = ROOT / "backend/src/main/kotlin/com/alhadi/cmms/backend/Application.kt"
application = application_path.read_text(encoding="utf-8")
application = application.replace("import com.auth0.jwt.interfaces.DecodedJWT\n", "", 1)
old_current_user = '''private suspend fun ApplicationCall.currentUser(authService: AuthService) =
    principal<JWTPrincipal>()
        ?.payload
        ?.let { jwt -> authService.validateUser(jwt.subject, jwt.tokenVersion()) }
        ?: throw AuthenticationException()

private fun DecodedJWT.tokenVersion(): Int = getClaim("tokenVersion").asInt() ?: -1
'''
new_current_user = '''private suspend fun ApplicationCall.currentUser(authService: AuthService) =
    principal<JWTPrincipal>()
        ?.payload
        ?.let { payload ->
            authService.validateUser(
                payload.subject,
                payload.getClaim("tokenVersion").asInt() ?: -1
            )
        }
        ?: throw AuthenticationException()
'''
if application.count(old_current_user) != 1:
    raise RuntimeError("currentUser payload fix: expected one match")
application_path.write_text(application.replace(old_current_user, new_current_user, 1), encoding="utf-8")

# Commit the bootstrap user first, then write the audit entry on a normal connection.
replace_once(
    "backend/src/main/kotlin/com/alhadi/cmms/backend/auth/AuthService.kt",
    '''        repository.transaction { connection ->
            val organizationId = repository.findOrganizationId(config.defaultOrganizationCode, connection)
                ?: repository.createOrganization(
                    config.defaultOrganizationCode,
                    config.defaultOrganizationName,
                    connection
                )
            val passwordHash = PasswordHasher.hash(password.toCharArray())
            val user = repository.insertUser(
                organizationId = organizationId,
                name = config.bootstrapAdminName ?: "System Administrator",
                username = username,
                passwordHash = passwordHash,
                role = "Admin",
                connection = connection
            )
            repository.audit(
                organizationId = user.organizationId,
                userId = user.id,
                action = "Bootstrap",
                entityType = "User",
                details = "Initial administrator created",
                ipAddress = null
            )
        }''',
    '''        val user = repository.transaction { connection ->
            val organizationId = repository.findOrganizationId(config.defaultOrganizationCode, connection)
                ?: repository.createOrganization(
                    config.defaultOrganizationCode,
                    config.defaultOrganizationName,
                    connection
                )
            val passwordHash = PasswordHasher.hash(password.toCharArray())
            repository.insertUser(
                organizationId = organizationId,
                name = config.bootstrapAdminName ?: "System Administrator",
                username = username,
                passwordHash = passwordHash,
                role = "Admin",
                connection = connection
            )
        }
        repository.audit(
            organizationId = user.organizationId,
            userId = user.id,
            action = "Bootstrap",
            entityType = "User",
            details = "Initial administrator created",
            ipAddress = null
        )''',
    "bootstrap transaction",
)

print("Backend stage 1 compilation and bootstrap fixes applied.")
