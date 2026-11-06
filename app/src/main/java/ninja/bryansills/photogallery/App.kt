package ninja.bryansills.photogallery

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import ninja.bryansills.photogallery.detail.Detail
import ninja.bryansills.photogallery.detail.DetailScreen
import ninja.bryansills.photogallery.search.Search
import ninja.bryansills.photogallery.search.SearchScreen
import ninja.bryansills.photogallery.ui.theme.PhotoGalleryTheme


@Composable
fun App() {
    PhotoGalleryTheme {
        val backStack = rememberNavBackStack(Search)

        NavDisplay(
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            backStack = backStack,
            onBack = backStack::removeLastOrNull,
            entryProvider = entryProvider {
                entry<Search> {
                    SearchScreen(
                        onItemClicked = { id, title ->
                            backStack.add(Detail(id, title))
                        }
                    )
                }
                entry<Detail> { key ->
                    DetailScreen(
                        detail = key,
                        onBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
            }
        )
    }
}
