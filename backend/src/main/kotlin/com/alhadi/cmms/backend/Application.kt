package com.alhadi.cmms.backend

import com.alhadi.cmms.backend.auth.AuthRepository
import com.alhadi.cmms.backend.auth.AuthService
import com.alhadi.cmms.backend.config.AppConfig
import com.alhadi.cmms.backend.db.Database
import com.alhadi.cmms.backend.model.ApiError
import com.alhadi.cmms.backend.model.HealthResponse
import com.alhadi.cmms.backend.model.LoginRequest
import com.alhadi.cmms.backend.model.LogoutRequest
import com.alhadi.cmms.backend.model.RefreshRequest
import com.alhadi.cmms.backend.model.ServiceInfoResponse
import com.alhadi.cmms.backend.model.toResponse
import com.alhadi.cmms.backend.security.AuthenticationException
import com.alhadi.cmms.backend.security.AuthorizationException
import com.alhadi.cmms.backend.security.JwtService
import com.alhadi.cmms.backend.security.TooManyAttemptsException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.request.userAgent
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

private const val SERVICE_NAME = "alhadi-cmms-backend"
private const val SERVICE_VERSION = "0.1.0"
private val logger = LoggerFactory.getLogger("Application")

fun main() {
    val config = AppConfig.fromEnvironment()
    embeddedServer(
        factory = Netty,
        host = config.host,
        port = config.port,
        module = { module(config) }
    ).start(wait = true)
}

fun Application.module(config: AppConfig = AppConfig.fromEnvironment()) {
    val database = Database(config)
    val repository = AuthRepository(database)
    val jwtService = JwtService(config)
    val authService = AuthService(config, repository, jwtService)

    withContextBlocking { authService.bootstrapAdminIfNeeded() }
    withContextBlocking { authService.cleanup() }

    environment.monitor.subscribe(ApplicationStopped) {
        database.close()
    }

    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = false
            }
        )
    }
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("X-Request-ID")
        exposeHeader("X-Request-ID")
        config.corsHosts.forEach { host ->
            if (host == "*") anyHost() else allowHost(host, schemes = listOf("http", "https"))
        }
    }
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respondError(HttpStatusCode.BadRequest, "VALIDATION_ERROR", cause.message ?: "Invalid request")
        }
        exception<AuthenticationException> { call, cause ->
            call.respondError(HttpStatusCode.Unauthorized, "AUTHENTICATION_FAILED", cause.message ?: "Authentication failed")
        }
        exception<AuthorizationException> { call, cause ->
            call.respondError(HttpStatusCode.Forbidden, "ACCESS_DENIED", cause.message ?: "Access denied")
        }
        exception<TooManyAttemptsException> { call, cause ->
            call.respondError(HttpStatusCode.TooManyRequests, "TOO_MANY_ATTEMPTS", cause.message ?: "Try again later")
        }
        exception<Throwable> { call, cause ->
            logger.error("Unhandled request failure", cause)
            call.respondError(HttpStatusCode.InternalServerError, "INTERNAL_ERROR", "An unexpected error occurred")
        }
    }
    install(Authentication) {
        jwt("auth-jwt") {
            realm = config.jwtRealm
            verifier(jwtService.verifier)
            validate { credential ->
                val userId = credential.payload.subject ?: return@validate null
                val tokenVersion = credential.payload.getClaim("tokenVersion").asInt() ?: return@validate null
                val user = withContext(Dispatchers.IO) { authService.validateUser(userId, tokenVersion) }
                    ?: return@validate null
                JWTPrincipal(credential.payload)
            }
            challenge { _, _ ->
                call.respondError(HttpStatusCode.Unauthorized, "TOKEN_INVALID", "Access token is missing or invalid")
            }
        }
    }

    routing {
        get("/") {
            call.respond(
                mapOf(
                    "service" to SERVICE_NAME,
                    "version" to SERVICE_VERSION,
                    "api" to "/api/v1",
                    "health" to "/health/ready"
                )
            )
        }

        route("/health") {
            get("/live") {
                call.respond(
                    HealthResponse(
                        status = "UP",
                        service = SERVICE_NAME,
                        version = SERVICE_VERSION,
                        timestamp = Instant.now().toString()
                    )
                )
            }
            get("/ready") {
                val ready = withContext(Dispatchers.IO) { database.ping() }
                call.respond(
                    if (ready) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable,
                    HealthResponse(
                        status = if (ready) "UP" else "DOWN",
                        service = SERVICE_NAME,
                        version = SERVICE_VERSION,
                        timestamp = Instant.now().toString()
                    )
                )
            }
        }

        route("/api/v1") {
            route("/auth") {
                post("/login") {
                    val request = call.receive<LoginRequest>()
                    val response = withContext(Dispatchers.IO) {
                        authService.login(
                            organizationCode = request.organizationCode,
                            username = request.username,
                            password = request.password,
                            ipAddress = call.clientIp(),
                            userAgent = call.request.userAgent()
                        )
                    }
                    call.respond(response)
                }
                post("/refresh") {
                    val request = call.receive<RefreshRequest>()
                    val response = withContext(Dispatchers.IO) {
                        authService.refresh(request.refreshToken, call.clientIp(), call.request.userAgent())
                    }
                    call.respond(response)
                }
                post("/logout") {
                    val request = call.receive<LogoutRequest>()
                    withContext(Dispatchers.IO) { authService.logout(request.refreshToken, call.clientIp()) }
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            authenticate("auth-jwt") {
                get("/me") {
                    val user = call.currentUser(authService)
                    call.respond(user.toResponse())
                }
                get("/system/info") {
                    val user = call.currentUser(authService)
                    if (user.role !in setOf("Admin", "Supervisor")) {
                        throw AuthorizationException()
                    }
                    call.respond(
                        ServiceInfoResponse(
                            service = SERVICE_NAME,
                            version = SERVICE_VERSION,
                            environment = config.environment,
                            authenticatedUser = user.toResponse()
                        )
                    )
                }
            }
        }
    }
}

private suspend fun ApplicationCall.currentUser(authService: AuthService) =
    principal<JWTPrincipal>()
        ?.payload
        ?.let { payload ->
            authService.validateUser(
                payload.subject,
                payload.getClaim("tokenVersion").asInt() ?: -1
            )
        }
        ?: throw AuthenticationException()

private suspend fun ApplicationCall.respondError(status: HttpStatusCode, code: String, message: String) {
    val requestId = request.headers["X-Request-ID"] ?: UUID.randomUUID().toString()
    response.headers.append("X-Request-ID", requestId, safeOnly = false)
    respond(status, ApiError(code = code, message = message, requestId = requestId))
}

private fun ApplicationCall.clientIp(): String? =
    request.headers["X-Forwarded-For"]
        ?.substringBefore(',')
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: request.headers["X-Real-IP"]?.trim()?.takeIf { it.isNotEmpty() }

private fun withContextBlocking(block: () -> Unit) {
    block()
}
