package com.teapp

import com.teapp.models.Teahouse
import com.teapp.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.module() {
    val dataFactory = DatabaseFactory()
    install(Routing) {
        route("/api") {
            get("/teahouses/{id}") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val teaHouse = Teahouse(id)
                    if (teaHouse.fetchDataFromDB(dataFactory)) {
                        call.respond(teaHouse)
                    }
                } catch (invalidIdException: NumberFormatException) {
                }
            }
        }
        route("/") {
            get("/") {
                call.respond("This is Teapp mobile application Web-Server!")
            }
//            post("/login") {
//                lateinit var user: User
//                val jsonText = call.receiveText()
//                val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create()
//                val metaUserCredentials: UserCredentials = gson.fromJson(jsonText, UserCredentials::class.java)
//                val usersCredentials: ArrayList<UserCredentials> = dataFactory.getAllUsersCredentials()
//                val userCredentials: UserCredentials? = metaUserCredentials.checkUserLogin(usersCredentials)
//                if (userCredentials != null) user = dataFactory.getUserById(userCredentials.id)
//                else call.respond(HttpStatusCode.Forbidden, "Login or password is incorrect.\n Please try again!")
//                val userConnection: UserConnections = UserConnections((dataFactory.getAmountOfSessions() + 1), user)
//                val cookie: CookieToken = CookieToken(userConnection.session.accessToken.toString())
//                call.sessions.set<CookieToken>(cookie)
//                call.respond("You're logged in")
//            }
//            get("/logout") {
////                val cookie = call.sessions.get<CookieToken>()
//                call.sessions.clear<CookieToken>()
//            }
        }
    }
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
            serializeNulls()
        }
    }
//    install(Sessions) {
//        cookie<CookieToken>(AppStrings.COOKIE_NAME.value) {}
//    }
}

//fun getUserByCookie(cookie: CookieToken?): User? {
//    cookie!!.value ?: return null
//    lateinit var user: User
//    val userConnections: ArrayList<UserConnections>? = dataFactory.getAllSessions()
//    for (userConnection in userConnections!!) {
//        if (userConnection.session.accessToken.toString() == cookie.value &&
//            userConnection.session.expiredDate.toString() <= LocalDate.now().toString()) {
//            user = dataFactory.getUserById(userConnection.personId!!)
//        }
//    }
//    return user
//}
//
//fun checkCookie(cookie: CookieToken?): Boolean {
//    if(getUserByCookie(cookie)!! == null) return false
//    return true
//}