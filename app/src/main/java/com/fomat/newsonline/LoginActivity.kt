package com.fomat.newsonline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.fomat.newsonline.Models.News
import com.fomat.newsonline.Models.NewsData
import com.fomat.newsonline.Utils.SessionManager
import com.fomat.newsonline.services.ApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    lateinit var service : ApiService
    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnLogin : Button = findViewById(R.id.btnLogin)
        sessionManager = SessionManager(this)

        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("http://api.mediastack.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create<ApiService>(ApiService::class.java)

        btnLogin.setOnClickListener(){
            Toast.makeText(this, "You clicked me.", Toast.LENGTH_SHORT).show()
            Log.i("respuesta: ", "AUTENTICADO !!!!!!!!")
            getCathegorizedNews()
        }
    }

    fun getCathegorizedNews(){
        service.getCathegorizedNews().enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>?, response: Response<NewsData>?) {
                val posts = response?.body()
                Log.i("respuesta: ", Gson().toJson(posts))
                if (posts?.data != null) {
                    val gson = Gson()
                    val dataList: List<News> = posts?.data
                    val jsonText = gson.toJson(dataList)
                    sessionManager.saveNewsList(jsonText)
                    callMainActivity()
                } else {
                    Log.i("Error>>", Gson().toJson(posts))
                }
            }
            override fun onFailure(call: Call<NewsData>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    fun callMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}