package com.teapp

import com.teapp.models.AppStrings
import com.teapp.models.Teahouse
import com.teapp.models.UserConnections
import com.teapp.models.UserCredentials
import com.teapp.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.*
import io.ktor.sessions.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    install(Sessions){
        cookie<UserConnections>(AppStrings.COOKIE_NAME.value)
    }
    install(Routing) {
        //doLogin(dataFactory)
        route("/api") {
            get("/teahouses/{id}") {
                try{
                    val userConnections = UserConnections(1)
                    userConnections.userId = 999
                    call.sessions.set<UserConnections>(userConnections)
                    val id = call.parameters["id"]!!.toInt()
                    val teahouse = Teahouse(id)
                    if(teahouse.fetchDataFromDB(dataFactory)) {
                        call.respond(teahouse)
                    }
                }
                catch(invalidIdException: NumberFormatException) {}
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

