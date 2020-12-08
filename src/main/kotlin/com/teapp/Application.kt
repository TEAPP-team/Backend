package com.teapp

import com.teapp.models.*
import com.teapp.models.UserConnections
import com.teapp.service.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.*
import io.ktor.request.*
import io.ktor.sessions.*
import main.kotlin.com.teapp.service.LoginFeature
import java.lang.IllegalStateException

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
                    val id = call.parameters["id"]!!.toInt()
                    val teahouse = Teahouse(id)
                    if(teahouse.fetchDataFromDB(dataFactory)) {
                        call.respond(teahouse)
                    }
                }
                catch(invalidIdException: NumberFormatException) {}
            }
        }
        route("") {
            get("") {
                call.respond("Hello World!")
            }
            post("/login") {
                val multhiPart = call.receiveMultipart()
//                val credentials = LoginFeature().isCredentialsValid(multiPart, dataFactory)
//                val user = User(credentials.id)
                call.respond(multhiPart)
//                call.sessions.set(user)
            }
//            get("/") {
//                val session = call.sessions.get<UserConnections>()
//                if(session == null) {
//                    call.respond("You're not logged in")
//                    throw IllegalStateException()
//                }
//            }
            get("/logout") {
                call.sessions.clear<UserConnections>()
            }
        }

//        val userConnections = UserConnections(1)
//        userConnections.userId = 999
//        call.sessions.set<UserConnections>(userConnections)
    }
    install(ContentNegotiation) {
        gson{
            setPrettyPrinting()
            disableHtmlEscaping()
            serializeNulls()
        }
    }
}

