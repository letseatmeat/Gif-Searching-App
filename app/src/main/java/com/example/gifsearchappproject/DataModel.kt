package com.example.gifsearchappproject

class DataModel(var imageUrl: String) {

    fun getImgUrl(): String {
        return imageUrl
    }

    fun setImgUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }
}