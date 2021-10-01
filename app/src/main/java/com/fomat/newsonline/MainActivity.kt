package com.fomat.newsonline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.fomat.newsonline.Utils.Globals
import com.fomat.newsonline.Utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var navView : NavigationView
    private lateinit var usernameTv : TextView
    private lateinit var emailTv : TextView
    private lateinit var usernameImg : ImageView
    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        drawerLayout = findViewById(R.id.drawerLayout)
        navView= findViewById(R.id.nav_view)
        setProfileData()

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        replaceFragment(ProfileFragment(), "Profile")

        navView.setNavigationItemSelectedListener {
            it.isChecked = true
            when(it.itemId){
                R.id.nav_profile -> replaceFragment(ProfileFragment(), it.title.toString())
                R.id.nav_news -> replaceFragment(NewsFragment(), it.title.toString())
                R.id.nav_logout -> signOut()
            }
            true
        }
    }

    private fun setProfileData(){
        val firebaseUser = firebaseAuth.currentUser

        var headerView = navView.getHeaderView(0)
        usernameTv = headerView.findViewById(R.id.usernameTv)
        emailTv = headerView.findViewById(R.id.emailTv)
        usernameImg = headerView.findViewById(R.id.usernameImg)
        usernameTv.setText(firebaseUser!!.displayName)
        emailTv.setText(firebaseUser!!.email)
        Picasso.get().load(firebaseUser!!.photoUrl).into(usernameImg)
    }

    private fun replaceFragment(fragment: Fragment, title: String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }

    private fun signOut() {
        firebaseAuth.signOut()
        checkUser()
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            throwLoginActivity()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun throwLoginActivity(){
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
