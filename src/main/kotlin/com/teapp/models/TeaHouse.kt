package com.teapp.models

import com.google.gson.GsonBuilder
import main.kotlin.com.teapp.service.DatabaseFactory

data class TeaHouse(val id: Int) {
    lateinit var title: String
    lateinit var address: String
    var coordinates = Coordinates()
    var workTime = WorkTime()
    var phone: String? = null
    var site: String? = null
    var links: MutableList<Link>? = null

    class Coordinates {
        var latitude: Double? = null
        var longitude: Double? = null
    }

    class WorkTime{
        class DayWorkTime {
            var from: String? = null
            var to: String? = null
        }

        var weekdays: DayWorkTime = DayWorkTime()
        var weekend: DayWorkTime = DayWorkTime()
    }

    class Link {
        lateinit var title: String
        lateinit var link: String
        var icon_url: String? = null
    }

    /**
     * @return `true` if teahouse with this id exists & only.
     */
    fun fetchDataFromDB(db: DatabaseFactory): Boolean {
        return db.getTeahouseById(this)
    }

    fun toJson(): String {
        val gson = GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}