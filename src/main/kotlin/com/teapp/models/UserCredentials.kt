package main.kotlin.com.teapp.models

class UserCredentials(val id: Int) {
    lateinit var login: String
    lateinit var password: String

    fun isLoginValid(login: String): Boolean {
        return true
    }

    fun isPasswordValid(password: String): Boolean {
        return true
    }
}