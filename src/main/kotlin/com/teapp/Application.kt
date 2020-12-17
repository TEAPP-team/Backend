package com.teapp

import com.teapp.models.Teahouse
import com.teapp.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.*
import io.ktor.html.*
import io.ktor.html.HtmlContent
import io.ktor.request.*
import kotlinx.html.*
import kotlinx.html.stream.createHTML

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    install(Routing) {
        route("/api") {
            get("/teahouses/{id}") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val teahouse = Teahouse(id)
                    if (teahouse.fetchDataFromDB(dataFactory)) {
                        call.respond(teahouse)
                    }
                } catch (invalidIdException: NumberFormatException) {
                }
            }
            route("/admin") {
                get() {
                    call.respondHtml {
                        head {
                            title { "ADMIN" }
                        }
                        body {
                            form(action = "/api/admin/login") {
                                div { "user:" }
                                div { input(type = InputType.text, name = "username") }
                                div { "password:" }
                                div { input(type = InputType.password, name = "password") }
                                div { input(type = InputType.submit) }
                            }
                        }
                    }
                }

            }
            get("/admin/login") {
                val post = call.receiveParameters()
                if (post["username"] == "idonotknow" && post["password"] == "whymorgenshtern") {
                    call.respondText("You are welcome")
                } else {
                    call.respondText("You are not welcome")
                }
            }
        }
    }

    install(ContentNegotiation) {
        gson{
            setPrettyPrinting()
            disableHtmlEscaping()
            serializeNulls()
        }
    }
}

