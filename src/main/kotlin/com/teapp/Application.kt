package com.teapp

import com.teapp.models.Teahouse
import com.teapp.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    /*install(Sessions){
        cookie<UserCredentials>(AppStrings.COOKIE_NAME.value)
    }*/
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
    }
    install(ContentNegotiation) {
        gson{
            setPrettyPrinting()
            disableHtmlEscaping()
            serializeNulls()
        }
    }
}

