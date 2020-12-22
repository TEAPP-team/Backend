package com.teapp

import com.teapp.models.Teahouse
import com.teapp.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import com.teapp.models.Comment
import com.teapp.models.Post

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val dataFactory = DatabaseFactory
    install(Routing) {
        route("/api") {
            get("/teahouses/{id}") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val teahouse = Teahouse(id)
                    if (teahouse.fetchDataFromDB(dataFactory)) {
                        call.respond(teahouse)
                    }
                } catch (invalidIdException: NumberFormatException) {
                }
            }

            get("/posts/{id}") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val post = Post(id)
                    if (post.fetchDataFromDB(dataFactory)) {
                        call.respond(post)
                    }
                } catch (invalidIdException: NumberFormatException) {
                }
            }

            get("/posts_all/") {
                try {
                    val posts = mutableListOf<Post>()
                    if (dataFactory.getAllPosts(posts)) {
                        call.respond(posts)
                    }
                } catch (invalidIdException: NumberFormatException) {
                }
            }

            get("/comments/{id}") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val comment = Comment(id)
                    if (comment.fetchDataFromDB(dataFactory)) {
                        call.respond(comment)
                    }
                } catch (invalidIdException: NumberFormatException) {
                }
            }

            get("/comments_by_post/{id}") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val comments = mutableListOf<Comment>()
                    if (dataFactory.getCommentByPost(Post(id), comments)) {
                        call.respond(comments)
                    }
                } catch (invalidIdException: NumberFormatException) {
                }
            }
        }
    }
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
            serializeNulls()
        }
    }
}

