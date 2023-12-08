package com.muratozcan.instaclone.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.databinding.ActivityAuthBinding
import com.muratozcan.instaclone.databinding.ActivityMainBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}