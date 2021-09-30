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
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var service : ApiService
    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnLogin : Button = findViewById(R.id.btnLogin)
        sessionManager = SessionManager(this)
        val token = sessionManager.fetchGoogleToken()

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

        btnLogin.setOnClickListener {
            signIn()
        }

        if (token != null) {
            if (token.compareTo("")!=0){
                getCathegorizedNews()
            }
        }
    }

    fun getCathegorizedNews(){
        service.getCathegorizedNews(Globals.generalCategory).enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>?, response: Response<NewsData>?) {
                val posts = response?.body()
                Log.i("respuesta: ", Gson().toJson(posts))
                if (posts?.data != null) {
                    val gson = Gson()
                    val dataList: List<News> = posts?.data
                    val jsonText = gson.toJson(dataList)
                    sessionManager.saveNewsList(jsonText)
                    throwMainActivity()
                } else {
                    Log.i("Error>>", Gson().toJson(posts))
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
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, Globals.RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        /*if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }*/
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Globals.RC_SIGN_IN) {
            val result : GoogleSignInResult? = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                handleSignInResult(result)
            }
        }
    }

    private fun handleSignInResult(signInResult: GoogleSignInResult) {
        try {
            if(signInResult.isSuccess) {
                // Authenticated
                val account : GoogleSignInAccount? = signInResult.signInAccount

                // Signed in successfully
                val googleId = account?.id ?: ""
                Log.i("Google ID",googleId)

                val googleFirstName = account?.givenName ?: ""
                Log.i("Google First Name", googleFirstName)

                val googleLastName = account?.familyName ?: ""
                Log.i("Google Last Name", googleLastName)

                val googleEmail = account?.email ?: ""
                Log.i("Google Email", googleEmail)

                val googleProfilePicURL = account?.photoUrl.toString()
                Log.i("Google Profile Pic URL", googleProfilePicURL)

                val googleIdToken = account?.idToken ?: ""
                Log.i("Google ID Token", googleIdToken)
                sessionManager.saveGoogleToken(googleIdToken)
                getCathegorizedNews()
            } else {
                // Failed
                Log.i("RESULTADO FALLIDO", "RESIULTADO FALLIDO.")
            }

            /*val account = completedTask.getResult(
                ApiException::class.java
            )*/

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }
}