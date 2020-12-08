package main.kotlin.com.teapp.models

class Post(val id: Int) {
    lateinit var header: String
    lateinit var description: String
    lateinit var image: ByteArray
}