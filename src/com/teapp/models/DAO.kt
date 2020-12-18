package com.teapp.models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

class DAO {

    object Teahouses : Table() {
        val id: Column<Int> = integer("id").autoIncrement()
        val title: Column<String> = varchar("title", 45)
        val address: Column<String> = varchar("address", 200)
        val longitude: Column<Double?> = double("longitude").nullable()
        val latitude: Column<Double?> = double("latitude").nullable()
        val phone: Column<String?> = varchar("phone", 15).nullable()
        val site: Column<String?> = varchar("site", 150).nullable()

        override val primaryKey = PrimaryKey(id, name = "PK_Teahouse_ID")
    }

    object Links : Table() {
        val id: Column<Int> = integer("id").autoIncrement()
        val social_network_id: Column<Int> = (integer("social_network_id") references Social_Networks.id)
        val link: Column<String> = varchar("link", 300)
        val icon_url: Column<String?> = varchar("icon_url", 500).nullable()
        val teahouse_id: Column<Int> = (integer("teahouse_id") references Teahouses.id)

        override val primaryKey = PrimaryKey(id, name = "PK_Link_ID")
    }

    object Social_Networks : Table() {
        val id: Column<Int> = integer("id").autoIncrement()
        val social_network: Column<String> = varchar("social_network", 30)

        override val primaryKey = PrimaryKey(id, name = "PK_SocialNetwork_ID")
    }

//object Users : Table() {
//    val id: Column<Int> = integer("id").autoIncrement()
//    val firstName: Column<String> = varchar("firstname", 50)
//    val lastName: Column<String> = varchar("lastname", 50)
//    val avatar: Column<ExposedBlob> = blob("avatar")
//
//    override val primaryKey = PrimaryKey(Teahouses.id, name = "PK_Person_ID")
//}
//
//object UserCredentials : Table() {
//    val id: Column<Int> = integer("id").autoIncrement()
//    val login: Column<String> = varchar("login", 30)
//    val password: Column<String> = varchar("password", 64)
//
//    override val primaryKey = PrimaryKey(Teahouses.id, name = "PK_Credential_ID")
//}
//
//object UserConnections : Table() {
//    val id: Column<Int> = integer("id").autoIncrement()
//    val accessToken: Column<String> = varchar("access_token", 36)
//    //TODO Fill up parameter "expiredDate"
//    val expiredDate: Column<LocalDate>? = null/*varchar("exp_date", 30)*/
//    val userId: Column<Int> = integer("person_id")
//    val isLoggedOut: Column<Boolean> = bool("is_logged_out")
//}
}