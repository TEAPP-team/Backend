package com.teapp.models

import com.google.gson.GsonBuilder

data class Teahouse(val id: Int) {
    data class Link(val id: Int) {
        lateinit var social_network_type: String
        lateinit var link: String
        var icon_url: String? = null
        var teahause_id: Int = 0
    }

    lateinit var title: String
    lateinit var address: String
    var longitude: Double? = null
    var latitude: Double? = null
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