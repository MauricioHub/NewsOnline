package com.fomat.newsonline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.fomat.newsonline.Models.News
import com.fomat.newsonline.Models.NewsData
import com.fomat.newsonline.Utils.SessionManager
import com.fomat.newsonline.Services.ApiService
import com.fomat.newsonline.Utils.Globals
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var service : ApiService
    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnLogin : Button = findViewById(R.id.btnLogin)
        sessionManager = SessionManager(this)

        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(Globals.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create<ApiService>(ApiService::class.java)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Globals.GOOGLE_CLIENT_AUTH)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        btnLogin.setOnClickListener {
            signIn()
        }
    }

    private fun checkUser(){
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            getCathegorizedNews()
        }
    }

    fun getCathegorizedNews(){
        service.getCathegorizedNews(Globals.generalCategory).enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>?, response: Response<NewsData>?) {
                val posts = response?.body()
                Log.i(Globals.TAG, Gson().toJson(posts))
                if (posts?.data != null) {
                    val gson = Gson()
                    val dataList: List<News> = posts?.data
                    val jsonText = gson.toJson(dataList)
                    sessionManager.saveNewsList(jsonText)
                    throwMainActivity()
                } else {
                    Log.i(Globals.TAG, Gson().toJson(posts))
                }
            }
            override fun onFailure(call: Call<NewsData>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    fun throwMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, Globals.RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Globals.RC_SIGN_IN) {
            Log.d(Globals.TAG, "onActivityResult: Google signin intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            } catch (e: Exception){
                Log.d(Globals.TAG, "onAQctivityResult: " + e.message)
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(Globals.TAG, "FirebaseAuthWithGoogleAccount: begin firebase auth with gogle account")
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                if(authResult.additionalUserInfo!!.isNewUser){
                    Log.d(Globals.TAG, "Account created")
                } else{
                    Log.d(Globals.TAG, "Existing user")
                }
                getCathegorizedNews()
            }
            .addOnFailureListener { e ->
                Log.d(Globals.TAG, "firebaseAuthWithGoogleAccount: Login Failed due to:" + e.message)
            }
    }
}