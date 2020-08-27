package com.example.meadote.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.meadote.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initDrawerLayoutAndNavigation()
    }

    private fun initDrawerLayoutAndNavigation() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(
        menu: Menu?
    ): Boolean {
        menuInflater.inflate(
            R.menu.activity_main_toolbar,
            menu
        )
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
    /*  when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(this, "teste", Toast.LENGTH_SHORT).show()
            }
        } */
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {
        return when (item.itemId) {
            R.id.itBusca -> {
                Toast.makeText(
                    this,
                    "Em Breve",
                    Toast.LENGTH_LONG
                ).show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}