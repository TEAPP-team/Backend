package com.teapp.service

import com.teapp.models.Session
import com.teapp.models.User
import com.teapp.models.UserCredentials
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import java.util.*

class Auth {
    private val userCredentialsList: MutableList<UserCredentials> = mutableListOf(
        UserCredentials("user", "student", 1),
        UserCredentials("test", "yes", 2),
    )

    private val userList: MutableList<User> = mutableListOf(
        User(1, "Alexander", "Belozubov"),
        User(2, "Mikhail", "Komarov"),
    )

//    private val sessionsList: MutableList<Session> = mutableListOf()

    private fun getUserByCredentials(login: String, password: String): User? {
        val userCredentialsPair = userCredentialsList.find {it.login == login && it.password == password} ?: throw NullPointerException("User not found!")
        return userList.find { it.id == userCredentialsPair.id }
    }

    fun authenticate(login: String, password: String): Session {
        val user = getUserByCredentials(login, password)!!
        val newUserSession = Session(UUID.randomUUID().toString(), user.id)
//        sessionsList.add(newUserSession)
//        return newUserSession.token
        return newUserSession
    }

//    fun checkAuthenticate(token: String): User? {
    fun checkAuthenticate(token: String, sessionsList: MutableList<Session>): User? {
        val userSession = sessionsList.find{it.token == token} ?: throw NullPointerException()
        return userList.find { it.id == userSession.userId }
    }
//    TODO: Check token is not expired
}