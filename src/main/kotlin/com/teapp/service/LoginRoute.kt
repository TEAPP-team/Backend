package main.kotlin.com.teapp.service

import com.teapp.models.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import com.teapp.service.DatabaseFactory
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

fun Route.doLogin(dataFactory: DatabaseFactory){
    post("/login") {
        val multiPart = call.receiveMultipart()
        val credentials = isCredentialsValid(multiPart, dataFactory)
        val user = User(credentials.id)

        call.sessions.set(user)
    }
    get("/") {
        val session = call.sessions.get<UserConnections>()
        if(session == null) {
            call.respond("You're not logged in")
            throw IllegalStateException()
        }
    }
    get("/logout") {

        call.sessions.clear<UserConnections>()
    }
}

suspend fun getCredentials(multipart: MultiPartData): List<String> {
    var id: Int = 0
    var login: String = AppStrings.EMPTY_STRING.value
    var password: String = AppStrings.EMPTY_STRING.value
    while(true) {
        val part = multipart.readPart()?: break
        when(part) {
            is PartData.FormItem -> {
                if (part.name == AppStrings.ID_NAME.value)
                    id = part.value.toInt()
                if (part.name == AppStrings.LOGIN_NAME.value)
                    login = part.value
                if (part.name == AppStrings.PASSWORD_NAME.value)
                    password = part.value
            }
            is PartData.FileItem -> {}
        }
        part.dispose()
    }
    return listOf(id.toString(), login, password)
}

suspend fun isCredentialsValid(multipart: MultiPartData, dataFactory: DatabaseFactory): UserCredentials {
    var list = getCredentials(multipart)
    var credentials = UserCredentials(list[AppInts.ID_INDEX.value].toInt())
    credentials.login = list[AppInts.LOGIN_INDEX.value]
    credentials.password = list[AppInts.PASSWORD_INDEX.value]
    if(!credentials.isLoginValid(credentials.login, dataFactory)!!) throw IllegalArgumentException()
    return credentials
}