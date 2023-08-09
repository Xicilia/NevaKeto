package com.example.nevaketo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class FullScreenImage : AppCompatActivity() {

    private val mainImage: ImageView by lazy {
        findViewById(R.id.mainImage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        mainImage.setImageResource(R.drawable.test)
    }
}