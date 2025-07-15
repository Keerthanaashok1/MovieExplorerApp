package com.example.movielist

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    val movieLists by viewModel.paginatedMovieLists.collectAsState()

    val categoryTitles = mapOf(
        "now_playing" to "Now Playing",
        "popular" to "Popular",
        "top_rated" to "Top Rated",
        "upcoming" to "Upcoming"
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(categoryTitles.keys.toList()) { category ->
            val movies = movieLists[category] ?: emptyList()
            if (movies.isNotEmpty()) {
                MovieCarousel(
                    title = categoryTitles[category]!!,
                    movies = movies,
                    navController = navController,
                    onLoadMore = { viewModel.loadMoreMovies(category) }
                )
            }
        }
    }
}


@Composable
fun MovieCarousel(
    title: String,
    movies: List<Movie>,
    navController: NavController,
    onLoadMore: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState, movies) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= movies.size - 2 && movies.isNotEmpty()) {
                    Log.d("Pagination", "Carousel '$title': Reached item $lastVisibleIndex. Triggering load more.")
                    onLoadMore()
                }
            }
    }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                MoviePosterCard(movie = movie, onClick = {
                    navController.navigate(Screen.Details.createRoute(movie.id))
                })
            }
        }
    }
}

@Composable
fun MoviePosterCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            AsyncImage(
                model = ApiService.IMAGE_BASE_URL + movie.posterPath,
                contentDescription = movie.title,
                modifier = Modifier
                    .height(210.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = movie.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = String.format("%.1f", movie.voteAverage), fontSize = 14.sp)
                }
            }
        }
    }
}
