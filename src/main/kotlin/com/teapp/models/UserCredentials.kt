package com.teapp.models

class UserCredentials(val id: Int) {
    lateinit var login: String
    lateinit var password: String

    fun checkUserCredentials(usersCredentials: List<UserCredentials>): UserCredentials? {
        var loginIsValid = false
        lateinit var userCredentials: UserCredentials
        for (credentials in usersCredentials)
            if(login == credentials.login) {
                loginIsValid = true
                userCredentials = credentials
            }
        if(loginIsValid && password == userCredentials.password) return userCredentials
        return null
    }
}