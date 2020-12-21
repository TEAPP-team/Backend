package com.teapp.service

import com.teapp.UserSession
import com.teapp.models.Session
import com.teapp.models.User
import com.teapp.models.UserCredentials
import java.util.*

class AuthService {
    private val userCredentialsList: MutableList<UserCredentials> = mutableListOf(
        UserCredentials("user", "student", 1),
        UserCredentials("test", "yes", 2),
    )

    private val userList: MutableList<User> = mutableListOf(
        User(1, "Alexander", "Ivanon"),
        User(2, "Mikhail", "Komarov"),
    )

    private val sessionsList: MutableList<Session> = mutableListOf()

    private fun getUserByCredentials(login: String, password: String): User? {
        val userCredentialsPair = userCredentialsList.find {it.login == login && it.password == password} ?: throw NullPointerException("User with this login-password pair not found!")
        return userList.find { it.id == userCredentialsPair.id }
    }

    fun authenticate(requestLogin: String?, requestPassword: String?): String {
        if (requestLogin == null || requestPassword == null)
            throw NullPointerException("Login or password wasn't set")
        else {
            val user = getUserByCredentials(requestLogin, requestPassword)!!
            val newSession = Session(UUID.randomUUID().toString(), user.id)
            sessionsList.add(newSession)
            return newSession.token
        }

    }

    //    TODO: Check token is not expired
    fun isAuthenticated(cookie: Any?): Boolean {
        if(cookie is UserSession)
            return sessionsList.find{it.token == cookie.token} is Session
        return false
    }
}