package com.teapp.models

//class UserCredentials(val id: Int) {
class UserCredentials(val login: String, val password: String, val id: Int) {

}
//    {var login: String = ""
//    var password: String = ""

//    fun checkUserLogin(usersCredentials: ArrayList<UserCredentials>): UserCredentials? {
//        var loginIsValid = false
//        lateinit var userCredentials: UserCredentials
//        for (credentials in usersCredentials)
//            if(login == credentials.login) {
//                loginIsValid = true
//                userCredentials = credentials
//            }
//        if(loginIsValid && password == userCredentials.password) return userCredentials
//        return null
//    }}
