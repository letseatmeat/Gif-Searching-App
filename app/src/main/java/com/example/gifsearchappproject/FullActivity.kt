package com.example.gifsearchappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import com.bumptech.glide.Glide

class FullActivity : AppCompatActivity() {
    private lateinit var fullImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full)

        fullImageView = findViewById(R.id.fullImage)

        val receiver: Intent = intent
        val sourceUrl: String? = receiver.getStringExtra("imageUrl")

        Glide.with(this).load(sourceUrl).into(fullImageView)
    }
}