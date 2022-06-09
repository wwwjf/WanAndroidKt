package com.wwwjf.wanandroidkt

import android.graphics.Path
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.wwwjf.base.KLog
import com.wwwjf.wanandroidkt.databinding.ActivityDrawerBinding

class DrawerActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDrawer.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener(){

        })

        navView.setNavigationItemSelectedListener {
            KLog.e("-----------------------${it}")
            val result:Boolean = !(it.itemId == R.id.nav_home ||it.itemId == R.id.nav_gallery||it.itemId == R.id.nav_slideshow)
            //返回true，不继续处理后续点击事件，false 继续处理后续点击事件
            result
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            KLog.e("================${destination.label},$arguments")
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_jetpack_demo,R.id.nav_ivy_demo),
            drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}