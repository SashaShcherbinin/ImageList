package feature.photos.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photo: Photo,
) {
    val navController = LocalNavigation.current

    PhotoDetailContent(
        photo = photo,
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoDetailContent(
    photo: Photo,
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = photo.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.general_ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = rememberAsyncImagePainter(photo.largeUrl),
                    contentDescription = photo.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Fit,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = photo.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Owner: ${photo.owner}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (photo.dateTaken != null) {
                        Text(
                            text = "Date taken: ${photo.dateTaken}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Text(
                            text = "Date taken: Not available",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(name = "Photo Detail - With Date")
@Composable
private fun PhotoDetailScreenPreview_WithDate() {
    PreviewAppTheme {
        PhotoDetailContent(
            photo = Photo(
                id = "1",
                title = "Beautiful Sunset",
                owner = "photographer1",
                dateTaken = "2024-01-15",
                thumbnailUrl = "https://via.placeholder.com/150",
                largeUrl = "https://via.placeholder.com/800",
            )
        )
    }
}

@Preview(name = "Photo Detail - No Date")
@Composable
private fun PhotoDetailScreenPreview_NoDate() {
    PreviewAppTheme {
        PhotoDetailContent(
            photo = Photo(
                id = "2",
                title = "Ocean View",
                owner = "photographer3",
                dateTaken = null,
                thumbnailUrl = "https://via.placeholder.com/150",
                largeUrl = "https://via.placeholder.com/800",
            )
        )
    }
}

