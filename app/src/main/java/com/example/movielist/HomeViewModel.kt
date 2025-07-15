package com.example.movielist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepository(application)
    private val categories = listOf("now_playing", "popular", "top_rated", "upcoming")
    private val pageSize = 10

    private val _fullMovieLists = mutableMapOf<String, List<Movie>>()

    private val _paginatedMovieLists = MutableStateFlow<Map<String, List<Movie>>>(emptyMap())
    val paginatedMovieLists = _paginatedMovieLists.asStateFlow()

    init {
        fetchAllMovies()
    }

    private fun fetchAllMovies() {
        viewModelScope.launch {
            categories.forEach { category ->
                val movies = repository.getMovies(category)
                _fullMovieLists[category] = movies
                _paginatedMovieLists.update { currentMap ->
                    currentMap + (category to movies.take(pageSize))
                }
            }
        }
    }

    fun loadMoreMovies(category: String) {
        val fullList = _fullMovieLists[category] ?: return
        val currentList = _paginatedMovieLists.value[category] ?: return

        // Prevent multiple rapid calls while loading
        if (currentList.size >= fullList.size) {
            Log.d("Pagination", "No more movies to load for '$category'.")
            return
        }

        val currentSize = currentList.size
        val nextItems = fullList.drop(currentSize).take(pageSize)

        if (nextItems.isNotEmpty()) {
            _paginatedMovieLists.update { currentMap ->
                currentMap + (category to (currentList + nextItems))
            }
            // ADDED: Log to confirm new items were loaded
            Log.d("Pagination", "Loaded ${nextItems.size} more movies for '$category'. New total: ${currentSize + nextItems.size}")
        }
    }
}

