package ninja.bryansills.photogallery.search

import androidx.lifecycle.SavedStateHandle
import androidx.paging.testing.asSnapshot
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import ninja.bryansills.photogallery.network.FakePageableFlickrService
import kotlin.test.Test

class SearchViewModelTest {
    @Test
    fun `items page as expected`() = runTest {
        val galleryItems = createViewModel(testScheduler).galleryItems
        val itemsSnapshot = galleryItems.asSnapshot {
            scrollTo(50)
        }
        assert(itemsSnapshot.size > 50) { "Paging should load in more gallery items as the user scrolls" }
    }

    @Test
    fun `query updates the paging`() = runTest {
        val viewModel = createViewModel(testScheduler)
        val galleryItems = viewModel.galleryItems

        viewModel.searchText = "mango"
        viewModel.search()

        val mangoSnapshot = galleryItems.asSnapshot {  }
        assert(mangoSnapshot.all { it.query == "mango" }) { "Search results should be for mangoes" }

        viewModel.searchText = "buffalo"
        viewModel.search()

        val buffaloSnapshot = galleryItems.asSnapshot {
            refresh()
        }
        assert(buffaloSnapshot.all { it.query == "buffalo" }) { "Search results should be for buffaloes" }
    }

    private fun createViewModel(testScheduler: TestCoroutineScheduler): SearchViewModel {
        return SearchViewModel(
            flickrService = FakePageableFlickrService(),
            ioDispatcher = StandardTestDispatcher(testScheduler),
            savedStateHandle = SavedStateHandle()
        )
    }
}