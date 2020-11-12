package com.teapp.models

import com.google.gson.GsonBuilder

data class Teahouse(val id: Int) {
    data class Link(val id: Int) {
        data class SocialNetwork(val id: Int) {
            val socialNetwork: String? = null
        }

        val social_network_type: String? = null
        val link: String? = null
        val icon_url: String? = null
        val teahause_id: Int? = null
    }

    var title: String? = null
    var longitude: Double? = null
    var latitue: Double? = null
    var phone: String? = null
    var site: String? = null
    var workday_opening: String? = null
    var workday_closing: String? = null
    var weekday_opening: String? = null
    var weekday_closing: String? = null
    var links: MutableList<Link>? = null

    fun toJson(): String {
        val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}