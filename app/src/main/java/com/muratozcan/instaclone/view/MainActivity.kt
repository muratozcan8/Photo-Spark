package com.muratozcan.instaclone.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var homeFragment: HomePageFragment
    private lateinit var shareFragment: ShareFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var profileFragment: ProfileFragment

    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeFragment = HomePageFragment()
        shareFragment = ShareFragment()
        searchFragment = SearchFragment()
        profileFragment = ProfileFragment()

        // Başlangıç fragmentını ayarla (HomeFragment olarak)
        activeFragment = homeFragment
        supportFragmentManager.beginTransaction().add(R.id.container, profileFragment, "ProfileFragment").hide(profileFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, searchFragment, "SearchFragment").hide(searchFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, shareFragment, "ShareFragment").hide(shareFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, homeFragment, "HomeFragment").commit()

        // Başlangıçta görünen fragmentı belirle
        supportFragmentManager.beginTransaction().show(activeFragment!!).commit()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homePage -> {
                    // Görünürdeki fragmentı gizle, HomeFragment'ı göster
                    supportFragmentManager.beginTransaction().hide(activeFragment!!).show(homeFragment).commit()
                    activeFragment = homeFragment
                    true
                }
                R.id.share -> {
                    // Görünürdeki fragmentı gizle, ShareFragment'ı göster
                    supportFragmentManager.beginTransaction().hide(activeFragment!!).show(shareFragment).commit()
                    activeFragment = shareFragment
                    true
                }
                R.id.search -> {
                    // Görünürdeki fragmentı gizle, SearchFragment'ı göster
                    supportFragmentManager.beginTransaction().hide(activeFragment!!).show(searchFragment).commit()
                    activeFragment = searchFragment
                    true
                }
                R.id.profile -> {
                    // Görünürdeki fragmentı gizle, ProfileFragment'ı göster
                    supportFragmentManager.beginTransaction().hide(activeFragment!!).show(profileFragment).commit()
                    activeFragment = profileFragment
                    true
                }
                else -> false
            }
        }
    }
}
