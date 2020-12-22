package com.teapp.service

import com.teapp.UserSession
import com.teapp.models.Session
import java.util.UUID

class AuthService {
    fun authenticate(requestLogin: String?, requestPassword: String?, db: DatabaseFactory): String? {
        if (requestLogin == null || requestPassword == null)
            throw NullPointerException("Login or password wasn't set")
        else {
            val userId = db.getUserIdByCredentials(requestLogin, requestPassword)
            if (userId == null) {
                return null
            }
            else {
                val newSession = Session(UUID.randomUUID().toString(), userId)
                db.addSession(newSession)
                return newSession.token
            }
        }
    }

//    TODO: Check if token is expired
    fun isAuthenticated(cookie: Any?, db: DatabaseFactory): Int? {
        if(cookie is UserSession)
            return db.isAuthenticated(cookie.token)
        return null
    }

    fun logout(cookie: Any?, db: DatabaseFactory) = db.logout((cookie as UserSession).token)
}