package com.teapp

import com.teapp.models.Teahouse
import com.teapp.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    install(Routing) {
        route("/api") {
            get("/teahouses/{id}") {
                try{
                    val id = call.parameters["id"]!!.toInt()
                    val teahouse = Teahouse(id)
                    if(teahouse.fetchDataFromDB(dataFactory)) {
                        call.respondText(teahouse.toJson(), contentType = ContentType.Application.Json)
                    }
                }
                catch(invalidIdException: NumberFormatException) {}
            }
        }
    }
}

