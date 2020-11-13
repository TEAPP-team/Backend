package com.teapp.service

import com.teapp.Config
import com.teapp.models.Teahouse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


object teahouses : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val title: Column<String> = varchar("title", 45)
    val longitude: Column<Double> = double("longitude")
    val latitude: Column<Double> = double("latitude")
    val phone: Column<String> = varchar("phone", 15)
    val site: Column<String> = varchar("site", 150)
    val worktime_id: Column<Int> = integer("worktime_id")

    override val primaryKey = PrimaryKey(id, name = "PK_Teahouse_ID")

    fun toMap(row: ResultRow): Map<String, Any?> {
        return mapOf(
            "id" to row[teahouses.id],
            "title" to row[teahouses.title],
            "longitude" to row[teahouses.longitude],
            "latitude" to row[teahouses.latitude],
            "phone" to row[teahouses.phone],
            "site" to row[teahouses.site],
            "worktime_id" to row[teahouses.worktime_id]
        )
    }
}

object links : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val social_network_id: Column<Int> = integer("social_network_id")
    val link: Column<String> = varchar("link", 300)
    val icon_url: Column<String> = varchar("icon_url", 500)
    val teahouse_id: Column<Int> = integer("teahouse_id")

    override val primaryKey = PrimaryKey(links.id, name = "PK_Link_ID")

}

object social_networks : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val social_network: Column<String> = varchar("social_network", 30)

    override val primaryKey = PrimaryKey(social_networks.id, name = "PK_SocialNetwork_ID")

}

object DatabaseFactory {
    init {
        Database.connect(Config.dataSource)
    }

    fun getTeahousById(id: Int): String {
        val teahouse = Teahouse(id)
        val teahouseData = transaction {
            teahouses.select { teahouses.id eq id }.map { teahouses.toMap(it) }
        }[0]
        teahouse.phone = teahouseData["phone"] as? String
        teahouse.latitude = teahouseData["latitude"] as? Double
        teahouse.longitude = teahouseData["longitude"] as? Double
        teahouse.phone = teahouseData["phone"] as? String

        return teahouse.toJson()
    }
}