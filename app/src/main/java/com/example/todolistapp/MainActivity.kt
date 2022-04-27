package com.example.todolistapp

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.todolistapp.Profile.ProfileFragment
import com.example.todolistapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var navEmail:TextView
    private val DEFAULT_IMAGE_URL = "https://pic.onlinewebfonts.com/svg/img_458488.png"
    private lateinit var navImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar.toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navViewMain.setNavigationItemSelectedListener(this)
        navImage = binding.navViewMain.getHeaderView(0).findViewById<ImageView>(R.id.imageViewNavProfile)
        navEmail = binding.navViewMain.getHeaderView(0).findViewById<TextView>(R.id.textViewNavEmail)
        setUserInformation()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_active_tasks -> Toast.makeText(this, "active tasks", Toast.LENGTH_SHORT).show()

            R.id.nav_connect_devices -> Toast.makeText(this, "connect devices", Toast.LENGTH_SHORT).show()

            R.id.nav_deleted -> Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show()

            R.id.nav_finished -> Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show()

            R.id.nav_profile ->
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerToolbar, ProfileFragment(this)).commit()

            R.id.nav_today -> Toast.makeText(this, "today", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateUi(imageBitmap: Bitmap){
        navImage.setImageBitmap(imageBitmap)
    }

    private fun setUserInformation(){
        if(auth.currentUser!=null){
            navEmail.text = auth.currentUser!!.email
            if(auth.currentUser!!.photoUrl!=null){
                Picasso.get().load(auth.currentUser!!.photoUrl).into(navImage)
            }else{
                Picasso.get().load(DEFAULT_IMAGE_URL).into(navImage)
            }
        }
    }
}