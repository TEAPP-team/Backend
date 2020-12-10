package com.teapp

import com.google.gson.GsonBuilder
import com.teapp.models.*
import com.teapp.models.UserConnections
import com.teapp.models.UserCredentials
import com.teapp.service.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.sessions.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    data class UserConnectionId(val id: Int, val login: String, val password: String)
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
            get("/.") {
                call.respond("Hello World!")
            }
            post("/login") {
                val jsonText = call.receiveText()
                val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create()
                val metaUserCredentials: UserCredentials = gson.fromJson(jsonText, UserCredentials::class.java)
                val usersCredentials: ArrayList<UserCredentials> = dataFactory.getAllUsersCredentials()
                val userCredentials = metaUserCredentials.checkUserCredentials(usersCredentials)
                if(userCredentials != null){
                    val user: User = dataFactory.getUserById(userCredentials.id)
                    call.respond("User ${user.id} - name is ${user.firstName} and surname is ${user.lastName}")
                }
                else call.respond(HttpStatusCode.Forbidden, "Login or password is incorrect.\n Please try again!")
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
    }
    install(ContentNegotiation) {
        gson{
            setPrettyPrinting()
            disableHtmlEscaping()
            serializeNulls()
        }
    }
}

