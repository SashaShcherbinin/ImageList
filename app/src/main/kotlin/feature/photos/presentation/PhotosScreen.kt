package feature.photos.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.R
import base.compose.local.LocalNavigation
import base.compose.theme.PreviewAppTheme
import coil.compose.rememberAsyncImagePainter
import feature.photos.domain.entity.Photo
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel = koinViewModel(),
) {
    val navController = LocalNavigation.current
    val state: PhotosState by viewModel.state.collectAsState()

    Screen(
        state = state,
        onSearch = { query -> viewModel.process(PhotosIntent.Search(query)) },
        onRetry = { viewModel.process(PhotosIntent.Retry) },
        onPhotoClick = { photo ->
            navController.navigate(photo)
        },
        onLoadMore = {
            viewModel.process(PhotosIntent.LoadMore)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    state: PhotosState,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onLoadMore: () -> Unit,
) {
    var searchText by rememberSaveable { mutableStateOf(state.searchQuery) }
    val gridState = rememberLazyGridState()

    LaunchedEffect(state.searchQuery) {
        if (searchText != state.searchQuery) {
            searchText = state.searchQuery
        }
    }

    LaunchedEffect(searchText) {
        delay(300)
        if (searchText != state.searchQuery) {
            onSearch(searchText)
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            lastVisibleIndex to gridState.layoutInfo.totalItemsCount
        }.collect { (lastVisibleIndex, totalItems) ->
            if (lastVisibleIndex != null
                && lastVisibleIndex >= totalItems - 3
                && totalItems > 0
            ) {
                onLoadMore()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search photos...") },
                        trailingIcon = {
                            if (searchText.isNotBlank()) {
                                IconButton(onClick = {
                                    searchText = ""
                                    onSearch("")
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.general_ic_close),
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                    )
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val contentState = state.contentState) {
                is ContentState.Loading -> {
                    LoadingState()
                }

                is ContentState.Content -> {
                    PhotoGrid(
                        photos = state.photos,
                        hasMore = state.hasMore,
                        onPhotoClick = onPhotoClick,
                        gridState = gridState,
                    )
                }

                is ContentState.Error -> {
                    ErrorState(
                        message = contentState.message,
                        onRetry = onRetry,
                    )
                }

                is ContentState.Empty -> {
                    EmptyState()
                }
            }
        }
    }
}

@Composable
private fun PhotoGrid(
    photos: List<Photo>,
    hasMore: Boolean,
    onPhotoClick: (Photo) -> Unit,
    gridState: LazyGridState,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(photos) { photo ->
            PhotoItem(photo = photo, onClick = { onPhotoClick(photo) })
        }
        if (hasMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.width(32.dp))
                }
            }
        }
    }
}

@Composable
private fun PhotoItem(
    photo: Photo,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberAsyncImagePainter(photo.thumbnailUrl),
            contentDescription = photo.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No photos found",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "Loading State")
@Composable
private fun PhotosScreenPreview_Loading() {
    PreviewAppTheme {
        Screen(
            state = PhotosState(
                contentState = ContentState.Loading,
                searchQuery = "",
                photos = emptyList(),
                hasMore = false,
            ),
            onSearch = {},
            onRetry = {},
            onPhotoClick = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "Content State")
@Composable
private fun PhotosScreenPreview_Content() {
    PreviewAppTheme {
        Screen(
            state = PhotosState(
                contentState = ContentState.Content,
                photos = listOf(
                    Photo(
                        id = "1",
                        title = "Beautiful Sunset",
                        owner = "photographer1",
                        dateTaken = "2024-01-15",
                        thumbnailUrl = "https://via.placeholder.com/150",
                        largeUrl = "https://via.placeholder.com/800",
                    ),
                    Photo(
                        id = "2",
                        title = "Mountain Landscape",
                        owner = "photographer2",
                        dateTaken = "2024-01-16",
                        thumbnailUrl = "https://via.placeholder.com/150",
                        largeUrl = "https://via.placeholder.com/800",
                    ),
                    Photo(
                        id = "3",
                        title = "Ocean View",
                        owner = "photographer3",
                        dateTaken = null,
                        thumbnailUrl = "https://via.placeholder.com/150",
                        largeUrl = "https://via.placeholder.com/800",
                    ),
                    Photo(
                        id = "4",
                        title = "City Lights",
                        owner = "photographer4",
                        dateTaken = "2024-01-17",
                        thumbnailUrl = "https://via.placeholder.com/150",
                        largeUrl = "https://via.placeholder.com/800",
                    ),
                ),
                hasMore = true,
                searchQuery = "nature",
            ),
            onSearch = {},
            onRetry = {},
            onPhotoClick = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "Error State")
@Composable
private fun PhotosScreenPreview_Error() {
    PreviewAppTheme {
        Screen(
            state = PhotosState(
                contentState = ContentState.Error("Failed to load photos. Please try again."),
                searchQuery = "nature",
                hasMore = false,
                photos = emptyList(),
            ),
            onSearch = {},
            onRetry = {},
            onPhotoClick = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "Empty State")
@Composable
private fun PhotosScreenPreview_Empty() {
    PreviewAppTheme {
        Screen(
            state = PhotosState(
                contentState = ContentState.Empty,
                searchQuery = "xyz123",
                hasMore = false,
                photos = emptyList(),
            ),
            onSearch = {},
            onRetry = {},
            onPhotoClick = {},
            onLoadMore = {},
        )
    }
}
