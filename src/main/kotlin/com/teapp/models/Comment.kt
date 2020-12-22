package com.teapp.models

import com.teapp.service.DatabaseFactory

class Comment(var id: Int) {
//    lateinit
    lateinit var message: String
    var post_id: Int? = null
    var person_id: Int? = null
    lateinit var date: String

    /**
     * @return `true` if teahouse with this id exists & only.
     */
    fun fetchDataFromDB(db: DatabaseFactory): Boolean {
        return db.getCommentById(this)
    }
}