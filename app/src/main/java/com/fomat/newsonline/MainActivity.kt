package com.fomat.newsonline

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fomat.newsonline.Models.News
import com.fomat.newsonline.Models.NewsData
import com.fomat.newsonline.Utils.SessionManager
import com.fomat.newsonline.services.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<News>
    private lateinit var sessionManager : SessionManager
    lateinit var imageId : Array<Int>
    lateinit var heading : Array<String>
    lateinit var markDate : Array<String>
    lateinit var service : ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        val gson = Gson()
        val jsonText: String? = sessionManager.fetchNewsList()
        val itemType = object : TypeToken<ArrayList<News>>() {}.type
        newArrayList = gson.fromJson<ArrayList<News>>(jsonText, itemType)

        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("http://api.mediastack.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create<ApiService>(ApiService::class.java)

        /*imageId = arrayOf(
            R.drawable.downloada,
            R.drawable.downloadb,
            R.drawable.imagesa,
            R.drawable.imagesb,
            R.drawable.imagesc,
            R.drawable.imagesd,
            R.drawable.imagese
        )

        heading = arrayOf(
            "notice 1",
            "notice 2",
            "notice 3",
            "notice 4",
            "notice 5",
            "notice 6",
            "notice 7"
        )

        markDate = arrayOf(
            "notice 1",
            "notice 2",
            "notice 3",
            "notice 4",
            "notice 5",
            "notice 6",
            "notice 7"
        )*/

        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        newRecyclerView.adapter = MyAdapter(newArrayList)

        //newArrayList = arrayListOf<News>()
        //getUserData()
        //getCathegorizedNews()
        //doLogin()
    }

    /*private fun getUserData() {
        for (i in imageId.indices){
            val news = News("", "", "", "",
                "", "", "", "",
                "", "")
            newArrayList.add(news)
        }
        newRecyclerView.adapter = MyAdapter(newArrayList)
    }

    fun doLogin(){
        service.login(email = "09243388284", password = "123456")
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>?, response: Response<UserResponse>?) {
                    val posts = response?.body()
                    Log.i("RESPUESTA>>>>>: ", Gson().toJson(posts))
                    Log.i("RESPUESTA-RESPONSE>>>>>: ", response.toString())
                    if (posts != null) {
                        Log.i("RESPUESTA-TOKEN>>>>>: ", posts.token)
                    }
                }
                override fun onFailure(call: Call<UserResponse>?, t: Throwable?) {
                    t?.printStackTrace()
                }
            })
    }

    fun getCathegorizedNews(){
        service.getCathegorizedNews().enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>?, response: Response<NewsData>?) {
                val posts = response?.body()
                Log.i("respuesta: ", Gson().toJson(posts))
            }
            override fun onFailure(call: Call<NewsData>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }*/
}