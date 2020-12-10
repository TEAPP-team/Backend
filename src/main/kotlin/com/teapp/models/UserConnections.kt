package com.teapp.models

import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.util.*

data class UserConnections(val id: Int) {
    var personId: Int? = null
    var session = SessionInfo()

    class SessionInfo {
        var accessToken: String? = null
        var expiredDate: String? = null
        var isLoggedOut: Boolean? = null
    }

    constructor(id: Int, user: User): this(id) {
        personId = user.id
        this.session.accessToken = UUID.randomUUID().toString()
        this.session.expiredDate = LocalDate.now().plusDays(1.toLong()).toString()
        this.session.isLoggedOut = false
    }

    fun toJson(): String {
        val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}