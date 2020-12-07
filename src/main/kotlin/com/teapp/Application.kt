package main.kotlin.com.teapp

import com.teapp.models.TeaHouse
import main.kotlin.com.teapp.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.*
import io.ktor.sessions.*
import main.kotlin.com.teapp.models.AppConstants
import main.kotlin.com.teapp.models.UserCredentials
import main.kotlin.com.teapp.service.doLogin

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    install(Sessions){
        cookie<UserCredentials>(AppConstants.COOKIE_NAME.value)
    }
    install(Routing) {
        doLogin()
        route("/api") {
            get("/teahouses/{id}") {
                try{
                    val id = call.parameters["id"]!!.toInt()
                    val teaHouse = TeaHouse(id)
                    if(teaHouse.fetchDataFromDB(dataFactory)) {
                        call.respond(teaHouse)
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

