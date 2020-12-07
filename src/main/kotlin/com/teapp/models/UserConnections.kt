package main.kotlin.com.teapp.models

import java.time.LocalDate

data class UserConnections(val id: Int) {
    var userSession = SessionInfo()
    var userId: Int? = null

    class SessionInfo {
        var accessToken: String? = null
        var expiredDate: LocalDate? = null
        var isLoggedOut = true
    }
}