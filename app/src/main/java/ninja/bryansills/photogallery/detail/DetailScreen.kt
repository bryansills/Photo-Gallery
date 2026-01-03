package ninja.bryansills.photogallery.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
data class Detail(val id: String)

@Composable
fun DetailScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text(text = "Hello detail!", modifier = Modifier.padding(innerPadding))
    }
}