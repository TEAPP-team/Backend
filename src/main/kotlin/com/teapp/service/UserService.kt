package com.teapp.service

import com.teapp.UserSession
import com.teapp.models.User

class UserService {
    fun getUser(cookie: Any, dataFactory: DatabaseFactory): User {
//        return dataFactory.getUserById((cookie as UserSession).token)
        return User(1, "Mikhail", "Komarov")
    }
}