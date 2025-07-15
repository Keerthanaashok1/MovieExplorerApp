package com.example.movielist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(movieId: Int, navController: NavController) {

    val viewModel: MovieDetailsViewModel = viewModel()

    LaunchedEffect(movieId) {
        viewModel.loadData(movieId)
    }
    val cast by viewModel.cast.collectAsState()
    val director by viewModel.director.collectAsState()
    val similarMovies by viewModel.similarMovies.collectAsState()
    val isInMyList by viewModel.isInMyList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.movieDetails.collectAsState().value?.title ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        val movieDetails by viewModel.movieDetails.collectAsState()

        movieDetails?.let { movie ->
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                item {
                    Box(contentAlignment = Alignment.BottomStart) {
                        AsyncImage(
                            model = ApiService.IMAGE_BASE_URL + movie.posterPath,
                            contentDescription = movie.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                movie.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    String.format("%.1f", movie.voteAverage),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                // Overview and My List Button
                item {
                    Column(Modifier.padding(16.dp)) {
                        Button(
                            onClick = { viewModel.toggleInMyList(movie.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                if (isInMyList) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (isInMyList) "In My List" else "Add to My List")
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(movie.overview, fontSize = 16.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Cast: ${cast.take(5).joinToString { it.name }}")
                        Text("Director: ${director ?: "N/A"}")
                    }
                }

                // Similar Movies
                item {
                    if (similarMovies.isNotEmpty()) {
                        MovieCarousel(
                            title = "Similar Movies",
                            movies = similarMovies,
                            navController = navController,
                            onLoadMore = {}
                        )
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
