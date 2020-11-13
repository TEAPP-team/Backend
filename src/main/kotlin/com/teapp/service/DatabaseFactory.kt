package com.teapp.service

import com.teapp.Config
import com.teapp.models.Teahouse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


object teahouses : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val title: Column<String> = varchar("title", 45)
    val address: Column<String> = varchar("address", 200)
    val longitude: Column<Double?> = double("longitude").nullable()
    val latitude: Column<Double?> = double("latitude").nullable()
    val phone: Column<String?> = varchar("phone", 15).nullable()
    val site: Column<String?> = varchar("site", 150).nullable()

    //    val worktime_id: Column<Int?> = (integer("worktime_id") references worktime.id).nullable()
    override val primaryKey = PrimaryKey(id, name = "PK_Teahouse_ID")
}

//object worktime:Table(){
//    val id:Column<Int> = integer("id").autoIncrement()
//    val weekdays_opening: Column<LocalTime> = time
//    override val primaryKey = PrimaryKey(worktime.id, name = "PK_Worktime_ID")
//}

object links : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val social_network_id: Column<Int> = (integer("social_network_id") references social_networks.id)
    val link: Column<String> = varchar("link", 300)
    val icon_url: Column<String?> = varchar("icon_url", 500).nullable()
    val teahouse_id: Column<Int> = (integer("teahouse_id") references teahouses.id)

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
        transaction {
            addLogger(StdOutSqlLogger)
            val teahouseData = teahouses.select { teahouses.id eq id }.toList()
            if (teahouseData.size == 1) {
                teahouse.title = teahouseData[0][teahouses.title]
                teahouse.address = teahouseData[0][teahouses.address]
                teahouse.latitude = teahouseData[0][teahouses.latitude]
                teahouse.longitude = teahouseData[0][teahouses.longitude]
                teahouse.phone = teahouseData[0][teahouses.phone]
                teahouse.site = teahouseData[0][teahouses.site]
            }
        }
        val socialNetworks: MutableMap<Int, String> = mutableMapOf()
        transaction {
            addLogger(StdOutSqlLogger)
            for (socialNetwork in social_networks.selectAll().toList()) {
                socialNetworks[socialNetwork[social_networks.id]] = socialNetwork[social_networks.social_network]
            }
        }
        transaction {
            addLogger(StdOutSqlLogger)
            val linksList = links.select { links.teahouse_id eq id }.toList()
            if (linksList.isNotEmpty()) {
                teahouse.links = mutableListOf()
                for (linkRow in linksList) {
                    val link = Teahouse.Link(linkRow[links.id])
                    link.icon_url = linkRow[links.icon_url]
                    link.link = linkRow[links.link]
                    link.teahause_id = id
                    link.social_network_type = socialNetworks[linkRow[links.social_network_id]]!!
                    teahouse.links!!.add(link)
                }
            }
        }
        return teahouse.toJson()
    }
}