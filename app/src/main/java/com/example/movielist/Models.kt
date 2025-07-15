package com.example.movielist

import com.google.gson.annotations.SerializedName

data class MovieResponse(val results: List<Movie>)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double
)

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    val genres: List<Genre>
)

data class Genre(val id: Int, val name: String)

data class CreditsResponse(val cast: List<CastMember>, val crew: List<CrewMember>)

data class CastMember(val id: Int, val name: String)

data class CrewMember(val id: Int, val name: String, val job: String)

