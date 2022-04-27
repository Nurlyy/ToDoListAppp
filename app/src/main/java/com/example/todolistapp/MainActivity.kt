package com.example.todolistapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.todolistapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar.toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navViewMain.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_active_tasks -> Toast.makeText(this, "active tasks", Toast.LENGTH_SHORT).show()

            R.id.nav_connect_devices -> Toast.makeText(this, "connect devices", Toast.LENGTH_SHORT).show()

            R.id.nav_deleted -> Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show()

            R.id.nav_finished -> Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show()

            R.id.nav_profile -> Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show()

            R.id.nav_today -> Toast.makeText(this, "today", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}