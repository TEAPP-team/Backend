package com.teapp

import com.teapp.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    routing {
        get("/{id}") {
            call.respondText("${dataFactory.getTeahousById(call.parameters["id"]!!.toInt())}", contentType = ContentType.Text.Plain)
        }
    }
}

