package net.skripsi.downsyndromtracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import net.skripsi.downsyndromtracker.databinding.ActivityMainBinding
import net.skripsi.downsyndromtracker.information.InformationFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val informationFragment = InformationFragment()
    private val homeFragment = HomeFragment()
    private val profileFragment = ProfileFragment()

    private val fm = supportFragmentManager

    private var defaultFragment: Fragment = informationFragment
    private lateinit var menu: Menu
    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.menu_home
        }
        setStatusBarColor()
        setUpBottomNavigation()
    }

    private fun setStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)
    }

    private fun setUpBottomNavigation() {
        fm.beginTransaction().add(R.id.fragmentContainer, informationFragment)
            .show(informationFragment).commit()
        fm.beginTransaction().add(R.id.fragmentContainer, homeFragment).hide(homeFragment).commit()
        fm.beginTransaction().add(R.id.fragmentContainer, profileFragment).hide(profileFragment)
            .commit()

        menu = binding.bottomNavigation.menu
        menuItem = menu.getItem(0)
        menuItem.isChecked = true

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_information -> {
                    loadFragment(0, informationFragment)
                    true
                }

                R.id.menu_home -> {
                    loadFragment(1, homeFragment)
                    true
                }

                R.id.menu_profile -> {
                    loadFragment(2, profileFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun loadFragment(position: Int, fragment: Fragment) {
        menuItem = menu.getItem(position)
        menuItem.isChecked = true
        fm.beginTransaction().hide(defaultFragment).show(fragment).commit()
        defaultFragment = fragment
    }
}