package com.teapp.service

import com.teapp.Config
import com.teapp.models.Teahouse
import com.teapp.models.Comment
import com.teapp.models.Post
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

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

object Posts : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val header: Column<String> = varchar("header", 255)
    val description: Column<String> = text("description")
    val image: Column<ExposedBlob?> = blob("image").nullable()

    override val primaryKey = PrimaryKey(id, name = "PK_Posts_ID")
}

object Comments : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val message: Column<String> = text("message")
    val post_id: Column<Int?> = integer("post_id").nullable()
    val person_id: Column<Int?> = integer("person_id").nullable()

    override val primaryKey = PrimaryKey(id, name = "PK_Posts_ID")
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
                    "WHERE Teahouses.id = $id;"
        ) { rs ->
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
        var isTeahouseExist = false
        val id = teahouse.id
        transaction {
            addLogger(StdOutSqlLogger)
            val teahouseData = Teahouses.select { Teahouses.id eq id }.toList()
            if (teahouseData.size == 1) {
                isTeahouseExist = true
                teahouseData.forEach { values ->
                    teahouse.title = values[Teahouses.title]
                    teahouse.address = values[Teahouses.address]
                    teahouse.coordinates.latitude = values[Teahouses.latitude]
                    teahouse.coordinates.longitude = values[Teahouses.longitude]
                    teahouse.phone = values[Teahouses.phone]
                    teahouse.site = values[Teahouses.site]
                    teahouse.workTime = getWorktime(id)
                    teahouse.links = getLinks(id)
                }
            }
        }
        return isTeahouseExist
    }

    fun getPostById(post: Post): Boolean {
        var isPostExist = false
        transaction {
            addLogger(StdOutSqlLogger)
            val postsList = Posts.select { Posts.id eq post.id }.toList()
            if (postsList.size == 1) {
                post.header = postsList[0][Posts.header]
                post.description = postsList[0][Posts.description]
                if (postsList[0][Posts.image] != null) {
                    post.image = Base64.getEncoder().encodeToString(postsList[0][Posts.image]!!.bytes)
                }
                TransactionManager.current().exec(
                    "SELECT date FROM Posts WHERE id = ${post.id};"
                ) { rs ->
                    rs.next()
                    post.date = rs.getString("date")
                }
                isPostExist = true
            }
        }
        return isPostExist
    }

    fun getCommentById(comment: Comment): Boolean {
        var isCommentExist = false
        transaction {
            addLogger(StdOutSqlLogger)
            val commentssList = Comments.select { Comments.id eq comment.id }.toList()
            if (commentssList.size == 1) {
                comment.message = commentssList[0][Comments.message]
                comment.post_id = commentssList[0][Comments.post_id]
                comment.person_id = commentssList[0][Comments.person_id]
                TransactionManager.current().exec(
                    "SELECT date FROM Comments WHERE id = ${comment.id};"
                ) { rs ->
                    rs.next()
                    comment.date = rs.getString("date")
                }
                isCommentExist = true
            }
        }
        return isCommentExist
    }

    fun getAllPosts(posts: MutableList<Post>): Boolean{
        var isPostsExist = false
        transaction {
            addLogger(StdOutSqlLogger)
            val postsList = Posts.selectAll().toList()
            postsList.forEach() { post_data ->
                val post = Post(post_data[Posts.id])
                post.header = post_data[Posts.header]
                post.description = post_data[Posts.description]
                if (postsList[0][Posts.image] != null) {
                    post.image = Base64.getEncoder().encodeToString(post_data[Posts.image]!!.bytes)
                }
                TransactionManager.current().exec(
                    "SELECT date FROM Posts WHERE id = ${post.id};"
                ) { rs ->
                    rs.next()
                    post.date = rs.getString("date")
                }
                posts.add(post)
                isPostsExist = true
            }
        }
        return isPostsExist
    }

    fun getCommentByPost(post: Post, comments: MutableList<Comment>): Boolean {
        var isCommentExist = false
        transaction {
            addLogger(StdOutSqlLogger)
            val commentList = Comments.select { Comments.post_id eq post.id }.toList()
            commentList.forEach { comment_data ->
                val comment = Comment(0)
                comment.id = comment_data[Comments.id]
                comment.message = comment_data[Comments.message]
                comment.post_id = comment_data[Comments.post_id]
                comment.person_id = comment_data[Comments.person_id]
                TransactionManager.current().exec(
                    "SELECT date FROM Comments WHERE id = ${comment.id};"
                ) { rs ->
                    rs.next()
                    comment.date = rs.getString("date")
                }
                isCommentExist = true
                comments.add(comment)
            }
        }
        return isCommentExist
    }

    fun addPost(post: Post) {
        transaction {
            addLogger(StdOutSqlLogger)
            Posts.insert {
                it[header] = post.header
                it[description] = post.description
                if (post.image != null) {
                    it[image] = ExposedBlob(Base64.getDecoder().decode(post.image))
                }
            }
        }
    }

    fun addComment(comment: Comment) {
        transaction {
            addLogger(StdOutSqlLogger)
            Comments.insert {
                it[message] = comment.message
                if (comment.person_id != null) {
                    it[person_id] = comment.person_id
                }
                if (comment.post_id != null) {
                    it[post_id] = comment.post_id
                }
            }
        }
    }
}