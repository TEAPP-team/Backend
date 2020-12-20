package com.teapp.models

import com.google.gson.GsonBuilder
import java.sql.Blob

data class User(val id: Int, val firstName: String, val lastName: String) {
//    var firstName: String = ""
//    var lastName: String = ""
//    var avatar: Blob? = null

//    fun toJson(): String {
//        val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create()
//        return gson.toJson(this)
//    }
}