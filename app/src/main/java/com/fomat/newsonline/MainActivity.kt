package com.fomat.newsonline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<News>
    lateinit var imageId : Array<Int>
    lateinit var heading : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageId = arrayOf(
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

        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf<News>()
        getUserData()
    }

    private fun getUserData() {
        for (i in imageId.indices){
            val news = News(imageId[i], heading[i])
            newArrayList.add(news)
        }
        newRecyclerView.adapter = MyAdapter(newArrayList)
    }
}