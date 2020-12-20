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
import io.ktor.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class UserSession(val token: String)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    val sessionsList: MutableList<Session> = mutableListOf()
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
//            transform(SessionTransportTransformerMessageAuthentication(hex("6819b57a326945c1968f45236581")))
        }
    }
    install(Routing) {
        route("/api") {
            get("/teahouses/{id}") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val teaHouse = Teahouse(id)
                    if (teaHouse.fetchDataFromDB(dataFactory)) {
                        call.respond(teaHouse)
                    }
                }
                catch (invalidIdException: NumberFormatException) {}
            }
        }
        route("/") {
            get {
                val greeting = "Welcome to Teapp Web-Server!"
//                val cookie = call.sessions.get<UserSession>() ?: "Welcome to Teapp Web-Server"
                call.respondText(greeting)
            }
        }
        route("/login") {
            post {
                val requestContentType = call.request.headers["Content-Type"]
                if (requestContentType is String) {
                    if (requestContentType == "application/x-www-form-urlencoded" || requestContentType.contains("multipart/form-data")) {
                        try {
//                            if(!Auth().isAuthenticated(call.sessions.get("SESSION_ID"))) {
//                                TODO: If is authenticated don't generate new token
                                val params = call.receive<Parameters>()
                                val requestLogin = params["login"] ?: throw NullPointerException("Login wasn't set")
                                val requestPassword = params["password"] ?: throw NullPointerException("Password wasn't set")
//                            val authToken = Auth().authenticate(requestLogin, requestPassword)
                                val userSession = Auth().authenticate(requestLogin, requestPassword)
                                sessionsList.add(userSession)
                                val authToken = userSession.token
                                println(authToken)
                                call.sessions.set("SESSION_ID", UserSession(authToken))
                                call.respondRedirect("/")
//                            }
//                            else
//                                call.respond("Authenticated yet!")
                        }
                        catch(ex: NullPointerException) {
                            call.respond(HttpStatusCode.BadRequest, ex.message!!)
                        }
                    }
                    else
                        if (listOf("application/json", "text/plain").contains(requestContentType)) {
//                            TODO: Realize auth
                            call.respond(HttpStatusCode.OK)
                        }
                    else
                        call.respond(HttpStatusCode.UnsupportedMediaType)
                }
            }
        }
        route("/profile") {
            get {
                val cookie = call.sessions.get("SESSION_ID")
                if(cookie is UserSession) {
                    try {
                        val user = Auth().checkAuthenticate(cookie.token, sessionsList)!!
                        call.respondText("Firstname: ${user.firstName}\nLastname: ${user.lastName}")
                    }
                    catch (ex: NullPointerException) {
                        call.respond(HttpStatusCode.Forbidden, "Access denied!")
                    }
                }
                else
                    call.respond(HttpStatusCode.Forbidden, "Access denied!")
            }
        }
        route("/logout") {
            post {
                call.sessions.clear("SESSION_ID")
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

