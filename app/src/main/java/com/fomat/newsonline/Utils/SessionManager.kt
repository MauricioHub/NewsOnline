package com.fomat.newsonline.Utils

import android.content.Context
import android.content.SharedPreferences
import com.fomat.newsonline.R

class SessionManager (context : Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val NEWS_LIST= "news_list"
    }

    fun saveNewsList(news : String){
        val editor = prefs.edit()
        editor.putString(NEWS_LIST, news)
        editor.apply()
    }

    fun fetchNewsList() : String? {
        return prefs.getString(NEWS_LIST, null)
    }
}