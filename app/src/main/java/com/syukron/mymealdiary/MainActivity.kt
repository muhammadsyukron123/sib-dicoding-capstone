package com.syukron.mymealdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.syukron.mymealdiary.util.ThemeProvider

class MainActivity : AppCompatActivity(R.layout.activity_main),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private var isUpButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val theme = ThemeProvider(this).getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        drawerLayout = findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.trackerFragment,
                R.id.calculatorFragment,
                R.id.nutrientFragment,
                R.id.settingsFragment,
                R.id.aboutFragment2
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    fun useUpButton() {
        supportActionBar?.setHomeAsUpIndicator(
            androidx.appcompat.R.drawable.abc_ic_ab_back_material
        )
        lockDrawerSlide(true)
        isUpButton = true
    }

    fun lockDrawerSlide(boolean: Boolean) {
        if (boolean) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    fun useHamburgerButton() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        isUpButton = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            isUpButton -> {
                if (item.itemId == android.R.id.home) {
                    navController.navigateUp()
                }
                true
            }
            drawerLayout.isDrawerVisible(GravityCompat.START) -> {
                drawerLayout.closeDrawers()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START, true)
        return item.onNavDestinationSelected(navController) ||
                super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}