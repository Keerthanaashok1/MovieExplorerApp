package com.example.movielist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepository(application)
    private val myListService = MyListService(application)

    private val _myMovies = MutableStateFlow<List<MovieDetails>>(emptyList())
    val myMovies = _myMovies.asStateFlow()

    fun loadMyList() {
        viewModelScope.launch {
            val movieIds = myListService.getMyList().mapNotNull { it.toIntOrNull() }
            val moviesList = mutableListOf<MovieDetails>()
            for (id in movieIds) {
                repository.getMovieDetails(id)?.let { movieDetails ->
                    moviesList.add(movieDetails)
                }
            }
            _myMovies.value = moviesList
        }
    }


    fun removeFromMyList(movieId: Int) {
        myListService.toggleMovie(movieId)
        loadMyList()
    }
}
