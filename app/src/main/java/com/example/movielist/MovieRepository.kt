package com.example.movielist

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

class MovieRepository(private val context: Context) {
    private val apiService: ApiService = RetrofitInstance.api
    private val gson = Gson()

    private fun <T> loadJsonFromAssets(fileName: String, clazz: Class<T>): T? {
        return try {
            context.assets.open(fileName).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    gson.fromJson(reader, clazz)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getMovies(category: String): List<Movie> {
        return try {
            val response = apiService.getMovies(category)
            if (response.isSuccessful) {
                response.body()?.results?.sortedBy { it.voteAverage } ?: emptyList()
            } else {
                loadJsonFromAssets("$category.json", MovieResponse::class.java)
                    ?.results
                    ?.sortedBy { it.voteAverage } ?: emptyList()
            }
        } catch (e: Exception) {
            loadJsonFromAssets("$category.json", MovieResponse::class.java)
                ?.results
                ?.sortedBy { it.voteAverage } ?: emptyList()
        }
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetails? {
        return try {
            val response = apiService.getMovieDetails(movieId)
            if (response.isSuccessful) response.body() else loadJsonFromAssets("movie_details.json", MovieDetails::class.java)
        } catch (e: Exception) {
            loadJsonFromAssets("movie_details.json", MovieDetails::class.java)
        }
    }

    suspend fun getCredits(movieId: Int): CreditsResponse? {
        return try {
            val response = apiService.getMovieCredits(movieId)
            if (response.isSuccessful) response.body() else loadJsonFromAssets("credits.json", CreditsResponse::class.java)
        } catch (e: Exception) {
            loadJsonFromAssets("credits.json", CreditsResponse::class.java)
        }
    }

    suspend fun getSimilarMovies(movieId: Int): List<Movie> {
        return try {
            val response = apiService.getSimilarMovies(movieId)
            if (response.isSuccessful) response.body()?.results ?: emptyList() else loadJsonFromAssets("similar.json", MovieResponse::class.java)?.results ?: emptyList()
        } catch (e: Exception) {
            loadJsonFromAssets("similar.json", MovieResponse::class.java)?.results ?: emptyList()
        }
    }
}
