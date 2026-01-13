package ninja.bryansills.photogallery.search

import androidx.lifecycle.SavedStateHandle
import androidx.paging.testing.asSnapshot
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import ninja.bryansills.photogallery.RepeatTest
import ninja.bryansills.photogallery.RepeatTestRule
import ninja.bryansills.photogallery.network.FakePageableFlickrService
import org.junit.Rule
import kotlin.test.Test

class SearchViewModelTest {
    @get:Rule() val repeatTestRule = RepeatTestRule()

    @Test
    fun `items page as expected`() = runTest {
        val galleryItems = createViewModel(testScheduler).galleryItems
        val itemsSnapshot = galleryItems.asSnapshot {
            scrollTo(50)
        }
        assert(itemsSnapshot.size > 50) { "Paging should load in more gallery items as the user scrolls" }
    }

    @RepeatTest(attemptCount = 1000)
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

//    @RepeatTest(attemptCount = 50)
//    @Test
//    fun `all fails`() {
//        assertEquals("not the same", "totally different")
//    }

    private fun TestScope.createViewModel(testScheduler: TestCoroutineScheduler): SearchViewModel {
        return SearchViewModel(
            flickrService = FakePageableFlickrService(),
            ioDispatcher = StandardTestDispatcher(testScheduler),
            vmScope = backgroundScope,
            savedStateHandle = SavedStateHandle()
        )
    }
}