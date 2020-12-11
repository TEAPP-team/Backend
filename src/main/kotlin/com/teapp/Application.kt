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
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    install(Sessions){
        cookie<String>(AppStrings.COOKIE_NAME.value) {}
    }
    install(Routing) {
        //doLogin(dataFactory)
        route("") {
            get("/.") { call.respond("Hello World!") }
            post("") {
                lateinit var user: User
                val jsonText = call.receiveText()
                val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create()
                val metaUserCredentials: UserCredentials = gson.fromJson(jsonText, UserCredentials::class.java)
                val usersCredentials: ArrayList<UserCredentials> = dataFactory.getAllUsersCredentials()
                val userCredentials: UserCredentials? = metaUserCredentials.checkUserLogin(usersCredentials)
                if(userCredentials != null) user = dataFactory.getUserById(userCredentials.id)
                else call.respond(HttpStatusCode.Forbidden, "Login or password is incorrect.\n Please try again!")
                val userConnection: UserConnections = UserConnections((dataFactory.getAmountOfSessions() + 1), user)
//                call.respond(userConnection.session.accessToken.toString())
                call.sessions.set(userConnection.session.accessToken.toString())
            }
            get("/logout") { call.sessions.clear<UserConnections>() }
        }
        route("/api") {
            get("/teahouses/{id}") {
                lateinit var user: User
                try{
//                    val cookie = call.sessions.get<String>()
//                    user = getUserByCookie()
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

