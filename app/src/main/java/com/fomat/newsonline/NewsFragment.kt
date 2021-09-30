package com.fomat.newsonline

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fomat.newsonline.Models.News
import com.fomat.newsonline.Models.NewsData
import com.fomat.newsonline.Services.ApiService
import com.fomat.newsonline.Utils.Globals
import com.fomat.newsonline.Utils.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<News>
    private lateinit var categoryArrayList: ArrayList<String>
    private lateinit var sessionManager : SessionManager
    private lateinit var contxt: Context
    lateinit var service : ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_news, container, false)

        contxt = requireContext()
        sessionManager = SessionManager(contxt)
        val gson = Gson()
        val jsonText: String? = sessionManager.fetchNewsList()
        val itemType = object : TypeToken<ArrayList<News>>() {}.type
        newArrayList = gson.fromJson<ArrayList<News>>(jsonText, itemType)
        categoryArrayList = loadCategories()

        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl(Globals.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create<ApiService>(ApiService::class.java)

        newRecyclerView = view.findViewById(R.id.recyclerView)
        setupRecycler(newArrayList)

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.setHasFixedSize(true)
        categoryRecyclerView.adapter = MyCategoryAdapter(categoryArrayList)

        categoryRecyclerView.addOnItemTouchListener(RecyclerCategoryClickListener(contxt,
            categoryRecyclerView, object : RecyclerCategoryClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Log.d("message>>", "I'M CATEGORY: " + categoryArrayList[position])
                getCathegorizedNews(categoryArrayList[position])
            }
            override fun onItemLongClick(view: View?, position: Int) {
                TODO("do nothing")
            }
        }))

        return view
    }

    private fun setupRecycler(newList : ArrayList<News>){
        newRecyclerView.layoutManager = LinearLayoutManager(contxt,  LinearLayoutManager.VERTICAL, false)
        newRecyclerView.setHasFixedSize(true)
        newRecyclerView.adapter = MyAdapter(newList)
    }

    private fun getCathegorizedNews(category: String){
        service.getCathegorizedNews(category).enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>?, response: Response<NewsData>?) {
                val posts = response?.body()
                Log.i("respuesta: ", Gson().toJson(posts))
                if (posts?.data != null) {
                    val dataList: ArrayList<News> = posts?.data
                    setupRecycler(dataList)
                } else {
                    Log.i("Error>>", Gson().toJson(posts))
                }
            }
            override fun onFailure(call: Call<NewsData>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    private fun loadCategories() : ArrayList<String>{
        var newCategoryList = ArrayList<String>()
        newCategoryList.add(Globals.generalCategory)
        newCategoryList.add(Globals.businessCategory)
        newCategoryList.add(Globals.entertainmentCategory)
        newCategoryList.add(Globals.healthCategory)
        newCategoryList.add(Globals.sportsCategory)
        newCategoryList.add(Globals.technologyCategory)
        return newCategoryList
    }

    class RecyclerCategoryClickListener(context: Context, recyclerView: RecyclerView, private val mListener: OnItemClickListener?) : RecyclerView.OnItemTouchListener {
        private val mGestureDetector: GestureDetector
        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
            fun onItemLongClick(view: View?, position: Int)
        }

        init {
            mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }
                override fun onLongPress(e: MotionEvent) {
                    val childView = recyclerView.findChildViewUnder(e.x, e.y)

                    if (childView != null && mListener != null) {
                        mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView))
                    }
                }
            })
        }

        override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
            val childView = view.findChildViewUnder(e.x, e.y)
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
            }
            return false
        }

        override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }
}