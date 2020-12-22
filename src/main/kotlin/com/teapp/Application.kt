package com.teapp

import com.teapp.models.*
import com.teapp.service.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.response.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.sessions.*
import io.ktor.sessions.Sessions

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class UserSession(val token: String)
data class Credentials(val login: String, val password: String)

@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    val authService = AuthService()
    val userService = UserService()
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
            serializeNulls()
        }
    }
    install(DefaultHeaders)
    install(Sessions) {
        cookie<UserSession>("SESSION_ID") {

        }
    }
    install(Routing) {
        route("/") {
            get {
                call.respondText("Welcome to Teapp Web-Server!")
            }
        }
        route("/api") {
            get("/teahouses/{id}") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val teahouse = Teahouse(id)
                    if (teahouse.fetchDataFromDB(dataFactory)) {
                        call.respond(teahouse)
                    }
                }
                catch (invalidIdException: NumberFormatException) {}
            }
            get("/teahouses/all") {
                call.respond(dataFactory.getAllTeahouses(dataFactory))
            }
        }
        route("/login") {
            post {
                if (authService.isAuthenticated(call.sessions.get("SESSION_ID"), DatabaseFactory) == null) {
                    val requestContentType = call.request.headers["Content-Type"]
                    if (requestContentType is String) {
                        if (requestContentType == "application/x-www-form-urlencoded" || requestContentType.contains("multipart/form-data")) {
                            try {
                                val params = call.receive<Parameters>()
                                val authToken = authService.authenticate(params["login"]!!, params["password"]!!, dataFactory)
                                if (authToken == null) {
                                    throw NullPointerException("User with this login-password pair not found!")
                                }
                                else {
                                    call.sessions.set(UserSession(authToken))
                                    call.respondRedirect("/profile")
                                }
                            }
                            catch (ex: NullPointerException) {
                                call.respond(HttpStatusCode.BadRequest, ex.message ?: "Login or password wasn't set")
                            }
                        }
                        else
                            if (listOf("application/json", "text/plain").contains(requestContentType)) {
                                try {
                                    val credentials = call.receive<Credentials>()
                                    val authToken = authService.authenticate(credentials.login, credentials.password, dataFactory)
                                    if (authToken == null) {
                                        throw NullPointerException("User with this login-password pair not found!")
                                    }
                                    else {
                                        call.sessions.set(UserSession(authToken))
                                        call.respond(HttpStatusCode.OK)
                                    }
                                }
                                catch (ex: NullPointerException) {
                                    call.respond(HttpStatusCode.BadRequest, ex.message ?: "Login or password wasn't set!")
                                }
                            }
                            else
                                call.respond(HttpStatusCode.UnsupportedMediaType)
                    }
                }
                else {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
        route("/profile") {
            get {
                val cookie = call.sessions.get("SESSION_ID")
                val userId = authService.isAuthenticated(cookie, dataFactory)
                if (userId != null) {
                    call.respond(userService.getUserById(userId, dataFactory))
                }
                else
                    call.respond(HttpStatusCode.Forbidden, "Access denied!")
            }
        }
        route("/logout") {
            post {
                val cookie = call.sessions.get("SESSION_ID")
                if (authService.isAuthenticated(cookie, dataFactory) is Int) {
                    authService.logout(cookie, dataFactory)
                }
                call.sessions.clear("SESSION_ID")
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

