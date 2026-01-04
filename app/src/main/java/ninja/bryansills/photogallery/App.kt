package ninja.bryansills.photogallery

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ninja.bryansills.photogallery.detail.Detail
import ninja.bryansills.photogallery.detail.DetailScreen
import ninja.bryansills.photogallery.search.Search
import ninja.bryansills.photogallery.search.SearchScreen
import ninja.bryansills.photogallery.ui.theme.PhotoGalleryTheme


@Composable
fun App() {
    PhotoGalleryTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Search) {
            composable<Search> {
                SearchScreen(
                    onItemClicked = { id, title ->
                        navController.navigate(Detail(id, title))
                    }
                )
            }
            composable<Detail> { navBackstackEntry ->
                val route: Detail = navBackstackEntry.toRoute()
                DetailScreen(
                    title = route.title.takeIf { it?.isNotBlank() == true },
                    onBack = {
                        navController.popBackStack<Detail>(inclusive = true)
                    }
                )
            }
        }
    }
}
