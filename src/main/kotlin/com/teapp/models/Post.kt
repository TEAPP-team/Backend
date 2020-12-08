package main.kotlin.com.teapp.models

class Post(val id: Int) {
    lateinit var header: String
    lateinit var description: String
    var image: String? =null
    lateinit var date: String
}