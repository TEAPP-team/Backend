package com.teapp.service

import com.teapp.Config
import com.teapp.models.Teahouse
import com.teapp.models.User
import com.teapp.models.UserCredentials
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

object TeaHouses : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val title: Column<String> = varchar("title", 45)
    val address: Column<String> = varchar("address", 200)
    val longitude: Column<Double?> = double("longitude").nullable()
    val latitude: Column<Double?> = double("latitude").nullable()
    val phone: Column<String?> = varchar("phone", 15).nullable()
    val site: Column<String?> = varchar("site", 150).nullable()

    override val primaryKey = PrimaryKey(id, name = "PK_TeaHouse_ID")
}

object Users : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val firstName: Column<String> = varchar("firstname", 50)
    val lastName: Column<String> = varchar("lastname", 50)
    val avatar: Column<ExposedBlob> = blob("avatar")

    override val primaryKey = PrimaryKey(TeaHouses.id, name = "PK_Person_ID")
}

object UserCredentials : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val login: Column<String> = varchar("login", 30)
    val password: Column<String> = varchar("password", 64)

    override val primaryKey = PrimaryKey(TeaHouses.id, name = "PK_Credential_ID")
}

object UserConnections : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val accessToken: Column<String> = varchar("access_token", 36)
    //TODO Fill up parameter "expiredDate"
    val expiredDate: Column<LocalDate>? = null/*varchar("exp_date", 30)*/
    val userId: Column<Int> = integer("person_id")
    val isLoggedOut: Column<Boolean> = bool("is_logged_out")
}

object Links : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val social_network_id: Column<Int> = (integer("social_network_id") references Social_Networks.id)
    val link: Column<String> = varchar("link", 300)
    val icon_url: Column<String?> = varchar("icon_url", 500).nullable()
    val teahouse_id: Column<Int> = (integer("teahouse_id") references TeaHouses.id)

    override val primaryKey = PrimaryKey(id, name = "PK_Link_ID")
}

object Social_Networks : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val social_network: Column<String> = varchar("social_network", 30)

    override val primaryKey = PrimaryKey(id, name = "PK_SocialNetwork_ID")
}

object DatabaseFactory {
    init {
        Database.connect(Config.dataSource)
    }

    private fun getWorktime(id: Int): Teahouse.WorkTime {
        val workTime = Teahouse.WorkTime()
        TransactionManager.current().exec(
            "SELECT COUNT(*) AS rowcount,\n" +
                "    DATE_FORMAT(Worktime.weekdays_opening, '%H.%i') as weekdays_opening,\n" +
                "    DATE_FORMAT(Worktime.weekdays_closing, '%H.%i') as weekdays_closing,\n" +
                "    DATE_FORMAT(Worktime.weekend_opening, '%H.%i') as weekend_opening,\n" +
                "    DATE_FORMAT(Worktime.weekend_closing, '%H.%i') as weekend_closing\n" +
                "FROM Worktime INNER JOIN Teahouses on Teahouses.Worktime_id = Worktime.id\n" +
                "WHERE Teahouses.id = $id;") { rs ->
            if (rs.next() && rs.getInt("rowcount") == 1) {
                workTime.weekdays.from = rs.getString("weekdays_opening")
                workTime.weekdays.to = rs.getString("weekdays_closing")
                workTime.weekend.from = rs.getString("weekend_opening")
                workTime.weekend.to = rs.getString("weekend_closing")
            }
        }
        return workTime
    }

    private fun getSocialNetworkTypes(): MutableMap<Int, String> {
        val socialNetworks: MutableMap<Int, String> = mutableMapOf()
        for (socialNetwork in Social_Networks.selectAll()) {
            socialNetworks[socialNetwork[Social_Networks.id]] = socialNetwork[Social_Networks.social_network]
        }
        return socialNetworks
    }

    //TODO Realize functions below, because they are just templates for testing---------------

    fun getUserById(id: Int): User {
        val user: User = User(1)
        user.firstName = "John"
        user.lastName = "Doe"
        return user
    }

    fun getAllUsersCredentials(): ArrayList<UserCredentials> {
        val credentials: UserCredentials = UserCredentials(0)
        credentials.login = "qwe"
        credentials.password = "asd"
        val usersCredentials: ArrayList<UserCredentials> = arrayListOf(credentials)
        for (i in 1..5) {
            val credentials: UserCredentials = UserCredentials(i)
            credentials.login = "qwe$i"
            credentials.password = "asd$i"
            usersCredentials.add(credentials)
        }
        return usersCredentials
    }

    fun getAmountOfSessions() = 6

    //TODO Before here-----------------------------------------------------

    private fun getLinks(id: Int): MutableList<Teahouse.Link>? {
        var links: MutableList<Teahouse.Link>? = null
        val socialNetworksTypes = getSocialNetworkTypes()
        val linksList = Links.select { Links.teahouse_id eq id }.toList()
        if (linksList.isNotEmpty()) {
            links = mutableListOf()
            for (linkRow in linksList) {
                val link = Teahouse.Link()
                link.title = socialNetworksTypes[linkRow[Links.social_network_id]]!!
                link.link = linkRow[Links.link]
                link.icon_url = linkRow[Links.icon_url]
                links.add(link)
            }
        }
        return links
    }

    fun getTeahouseById(teahouse: Teahouse): Boolean {
        var isTeaHouseExist = false
        val id = teahouse.id
        transaction {
            addLogger(StdOutSqlLogger)
            val teaHouseData = TeaHouses.select { TeaHouses.id eq id }.toList()
            if (teaHouseData.size == 1) {
                isTeaHouseExist = true
                teaHouseData.forEach{ values->
                    teahouse.title = values[TeaHouses.title]
                    teahouse.address = values[TeaHouses.address]
                    teahouse.coordinates.latitude = values[TeaHouses.latitude]
                    teahouse.coordinates.longitude = values[TeaHouses.longitude]
                    teahouse.phone = values[TeaHouses.phone]
                    teahouse.site = values[TeaHouses.site]
                    teahouse.workTime = getWorktime(id)
                    teahouse.links = getLinks(id)
                }
            }
        }
        return isTeaHouseExist
    }
}