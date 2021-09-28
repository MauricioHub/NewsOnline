package com.fomat.newsonline.Utils

import android.content.Context
import android.content.SharedPreferences
import com.fomat.newsonline.R

class SessionManager (context : Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val NEWS_LIST= "news_list"
        const val GOOGLE_TOKEN= "google_token"
    }

    fun saveNewsList(news : String){
        val editor = prefs.edit()
        editor.putString(NEWS_LIST, news)
        editor.apply()
    }

    fun saveGoogleToken(token : String){
        val editor = prefs.edit()
        editor.putString(GOOGLE_TOKEN, token)
        editor.apply()
    }

    fun fetchNewsList() : String? {
        return prefs.getString(NEWS_LIST, null)
    }

    fun fetchGoogleToken() : String? {
        return prefs.getString(GOOGLE_TOKEN, null)
    }
}