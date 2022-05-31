package com.example.todolistapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import com.example.todolistapp.Profile.ProfileFragment
import com.example.todolistapp.Tasks.Task
import com.example.todolistapp.Tasks.activeTasks.ActiveTasksFragment
import com.example.todolistapp.Tasks.deleted.DeletedFragment
import com.example.todolistapp.Tasks.favorite.FavoriteFragment
import com.example.todolistapp.Tasks.today.TodayFragment
import com.example.todolistapp.databinding.ActivityMainBinding
import com.example.todolistapp.notification.CHANNEL_ID
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlin.math.log

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var navEmail:TextView
    private val DEFAULT_IMAGE_URL = "https://pic.onlinewebfonts.com/svg/img_458488.png"
    private lateinit var navImage: ImageView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.toolbar.title = "Активные задачи"
        auth = Firebase.auth
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar.toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks/${auth.currentUser?.uid}")
        binding.navViewMain.setNavigationItemSelectedListener(this)
        navImage = binding.navViewMain.getHeaderView(0).findViewById<ImageView>(R.id.imageViewNavProfile)
        navEmail = binding.navViewMain.getHeaderView(0).findViewById<TextView>(R.id.textViewNavEmail)
        setUserInformation()
        createChannelID()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerToolbar, ActiveTasksFragment(this)).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_active_tasks -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerToolbar, ActiveTasksFragment(this)).commit()
                binding.toolbar.toolbar.title = "Активные задачи"
            }
            R.id.nav_favorite -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerToolbar, FavoriteFragment(this)).commit()
                binding.toolbar.toolbar.title = "Избранные"
            }

            R.id.nav_finished -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerToolbar, DeletedFragment(this)).commit()
                binding.toolbar.toolbar.title = "Выполненные"
            }
            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerToolbar, ProfileFragment(this)).commit()
                binding.toolbar.toolbar.title = "Профиль"
            }
            R.id.nav_today -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerToolbar, TodayFragment(this)).commit()
                binding.toolbar.toolbar.title = "Сегодня"
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateUi(imageBitmap: Bitmap, username: String){
        navEmail.setText(username)
        navImage.setImageBitmap(imageBitmap)
    }

    private fun setUserInformation(){
        if(auth.currentUser!=null){
            navEmail.text = auth.currentUser!!.displayName
            if(auth.currentUser!!.photoUrl!=null){
                Picasso.get().load(auth.currentUser!!.photoUrl).into(navImage)
            }else{
                Picasso.get().load(DEFAULT_IMAGE_URL).into(navImage)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannelID(){
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Task Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Notifications for tasks"
        manager.createNotificationChannel(channel)
    }
}