package com.teapp.service

import com.teapp.models.Post

class AdminService {

    fun addPost(userId: Int, post: Post, db: DatabaseFactory): Boolean {
        if(db.isAdmin(userId)) {
            db.addPost(post)
            return true
        }
        return false
    }
}