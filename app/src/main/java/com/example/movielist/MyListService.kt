package com.example.movielist

import android.content.Context

class MyListService(context: Context) {
    private val prefs = context.getSharedPreferences("MyListPrefs", Context.MODE_PRIVATE)
    private val key = "MyList"

    fun getMyList(): Set<String> {
        return prefs.getStringSet(key, emptySet()) ?: emptySet()
    }

    fun isMovieInList(id: Int): Boolean {
        return getMyList().contains(id.toString())
    }

    fun toggleMovie(id: Int) {
        val list = getMyList().toMutableSet()
        val idString = id.toString()
        if (list.contains(idString)) {
            list.remove(idString)
        } else {
            list.add(idString)
        }
        prefs.edit().putStringSet(key, list).apply()
    }
}
