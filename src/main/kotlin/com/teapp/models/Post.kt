package main.kotlin.com.teapp.models

import com.teapp.service.DatabaseFactory

class Post(val id: Int) {
    lateinit var header: String
    lateinit var description: String
    var image: String? =null
    lateinit var date: String

    /**
     * @return `true` if teahouse with this id exists & only.
     */
    fun fetchDataFromDB(db: DatabaseFactory): Boolean {
        return db.getPostById(this)
    }
}