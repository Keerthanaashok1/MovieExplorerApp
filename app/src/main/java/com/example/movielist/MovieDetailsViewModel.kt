package com.example.movielist


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepository(application)
    private val myListService = MyListService(application)

    private val _movieDetails = MutableStateFlow<MovieDetails?>(null)
    val movieDetails = _movieDetails.asStateFlow()

    private val _cast = MutableStateFlow<List<CastMember>>(emptyList())
    val cast = _cast.asStateFlow()

    private val _director = MutableStateFlow<String?>(null)
    val director = _director.asStateFlow()

    private val _similarMovies = MutableStateFlow<List<Movie>>(emptyList())
    val similarMovies = _similarMovies.asStateFlow()

    private val _isInMyList = MutableStateFlow(false)
    val isInMyList = _isInMyList.asStateFlow()

    fun loadData(movieId: Int) {
        viewModelScope.launch {
            _movieDetails.value = repository.getMovieDetails(movieId)
            val credits = repository.getCredits(movieId)
            _cast.value = credits?.cast ?: emptyList()
            _director.value = credits?.crew?.find { it.job == "Director" }?.name
            _similarMovies.value = repository.getSimilarMovies(movieId)
            _isInMyList.value = myListService.isMovieInList(movieId)
        }
    }

    fun toggleInMyList(movieId: Int) {
        myListService.toggleMovie(movieId)
        _isInMyList.value = myListService.isMovieInList(movieId)
    }
}
