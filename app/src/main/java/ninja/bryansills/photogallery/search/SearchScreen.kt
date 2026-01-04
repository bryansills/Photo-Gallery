package ninja.bryansills.photogallery.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable

@Serializable
data object Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onItemClicked: (String, String?) -> Unit) {
    val viewModel: SearchViewModel = hiltViewModel()
    val pagingItems = viewModel.galleryItems.collectAsLazyPagingItems()

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

            if (pagingItems.loadState.refresh == LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp).align(Alignment.Center)
                )
            }

            val scrollState = rememberSaveable(
                viewModel.lastSearch,
                saver = LazyGridState.Saver
            ) { LazyGridState() }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                contentPadding = PaddingValues(top = 96.dp),
                state = scrollState
            ) {
                items(count = pagingItems.itemCount) { index ->
                    val galleryItem = pagingItems[index]!!
                    AsyncImage(
                        modifier = Modifier.aspectRatio(1f).clickable {
                            onItemClicked(galleryItem.id, galleryItem.title)
                        },
                        model = galleryItem.url,
                        contentDescription = galleryItem.description,
                        contentScale = ContentScale.Crop
                    )
                }

                if (pagingItems.loadState.append == LoadState.Loading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp).align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}
