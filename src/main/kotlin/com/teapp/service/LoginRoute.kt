package main.kotlin.com.teapp.service

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.routing.*
import main.kotlin.com.teapp.models.UserCredentials
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

fun Route.doLogin(){
    post("/login") {
        val multipart = call.receiveMultipart()
        var credentials = isCredentialsValid(multipart)
    }
}

suspend fun isCredentialsValid(multipart: MultiPartData): UserCredentials {
    val log = LoggerFactory.getLogger("DoLogin")
    var id: Int = 0
    var login: String = ""
    var password: String = ""
    while(true) {
        val part = multipart.readPart()?: break
        when(part) {
            is PartData.FormItem -> {
                log.info("FormItem: ${part.name} = ${part.value}")
                if (part.name == "id")
                    id = part.value.toInt()
                if (part.name == "username")
                    login = part.value
                if (part.name == "password")
                    password = part.value
            }
            is PartData.FileItem -> {
                log.info("FileItem: ${part.name} -> ${part.originalFileName} of ${part.contentType}")
            }
        }
        part.dispose()
    }
    val credentials = UserCredentials(id)
    if(credentials.isLoginValid(login) && credentials.isPasswordValid(password)){
        credentials.login = login
        credentials.password = password
    }
    else throw IllegalArgumentException()
    return credentials
}

fun createToken() {}