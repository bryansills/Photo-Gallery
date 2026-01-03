package ninja.bryansills.photogallery.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable
import ninja.bryansills.photogallery.R
import ninja.bryansills.photogallery.resolve

@Serializable
data object Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onItemClicked: (String) -> Unit) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            DockedSearchBar(
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp).fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = viewModel.searchText,
                        onQueryChange = { viewModel.searchText = it },
                        onSearch = {
                            viewModel.search()
                            searchExpanded = false
                        },
                        expanded = searchExpanded,
                        onExpandedChange = { searchExpanded = it },
                        placeholder = { Text("Search for photos") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )
                },
                expanded = false, // we don't want to display search suggestions
                onExpandedChange = { searchExpanded = it },
                content = {}
            )

            val errorMessage = uiState.error
            if (uiState.isWorking) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(48.dp))
            } else if (errorMessage != null) {
                ErrorCard(
                    message = errorMessage.resolve(),
                    modifier = Modifier.align(Alignment.Center).widthIn(max = 240.dp)
                )
            } else if (uiState.items.isEmpty()) {
                ErrorCard(
                    message = stringResource(R.string.no_photos_message),
                    modifier = Modifier.align(Alignment.Center).widthIn(max = 240.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                contentPadding = PaddingValues(top = 96.dp)
            ) {
                items(uiState.items) { galleryItem ->
                    AsyncImage(
                        modifier = Modifier.aspectRatio(1f).clickable { onItemClicked(galleryItem.id) },
                        model = galleryItem.url,
                        contentDescription = galleryItem.description,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text(text = message, modifier = Modifier.padding(16.dp))
    }
}