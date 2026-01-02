package ninja.bryansills.photogallery

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ninja.bryansills.photogallery.search.Search
import ninja.bryansills.photogallery.search.SearchScreen
import ninja.bryansills.photogallery.ui.theme.PhotoGalleryTheme


@Composable
fun App() {
    PhotoGalleryTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Search) {
            composable<Search> {
                SearchScreen()
            }
        }
    }
}
