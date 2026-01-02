package ninja.bryansills.photogallery.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.serialization.Serializable

@Serializable
data object Search

@Composable
fun SearchScreen() {
    val viewModel: SearchViewModel = hiltViewModel()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text(text = "Hello world!", modifier = Modifier.padding(innerPadding))
    }
}