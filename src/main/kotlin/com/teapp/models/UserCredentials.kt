package com.teapp.models

import com.teapp.service.DatabaseFactory

class UserCredentials(val id: Int) {
    lateinit var login: String
    lateinit var password: String

    fun isLoginValid(login: String, dataFactory: DatabaseFactory): Boolean? {
        val logins: List<String>? = dataFactory.getAllLogins()
        return logins?.contains(login)
    }
}