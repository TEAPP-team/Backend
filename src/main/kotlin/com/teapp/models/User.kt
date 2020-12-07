package main.kotlin.com.teapp.models

import java.sql.Blob

data class User(val id: Int) {
    lateinit var firstName: String
    lateinit var lastName: String
    var avatar: Blob? = null
}