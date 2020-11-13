package com.teapp.service

import com.teapp.Config
import com.teapp.models.Teahouse
import com.teapp.service.Links.autoIncrement
import org.jetbrains.exposed.sql.*

object Teahouses : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val longitude: Column<Double> = double("longitude")
    val latitude: Column<Double> = double("latitude")
    val phone: Column<String> = varchar("phone", 15)
    val site: Column<String> = varchar("phone", 150)
    val worktime_id: Column<Int> = integer("worktime_id")
}

object Links : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val social_network_id: Column<Int> = integer("social_network_id")
    val link: Column<String> = varchar("link", 300)
    val icon_url: Column<String> = varchar("icon_url", 500)
    val teahouse_id: Column<Int> = integer("teahouse_id")
}

object SocialNetworks : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val social_network: Column<String> = varchar("social_network", 30)
}

object DatabaseFactory {
    init {
        Database.connect(Config.dataSource)
    }

    fun getTeahousById(id: Int): String {
        var teahouse = Teahouse(id)
        return teahouse.toJson()
    }
}