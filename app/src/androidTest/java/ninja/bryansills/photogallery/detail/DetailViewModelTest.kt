package ninja.bryansills.photogallery.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import app.cash.turbine.runTestTurbine
import com.googlecode.flickrjandroid.FlickrException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import ninja.bryansills.photogallery.network.FakeFlickrService
import ninja.bryansills.photogallery.network.PhotoDetails
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * This test is instrumented because of the limitation described in this comment:
 * https://issuetracker.google.com/issues/349807172#comment14
 *
 * This could be avoided by extracting code into separate Gradle modules that are either JVM-only or
 * KMP.
 */
class DetailViewModelTest {
    @Test
    fun showsDetailsWhenTheNetworkRequestSucceeds() = runTestTurbine {
        val expectedDetails = FakeFlickrService.FakePhotoDetails

        val viewModel = createViewModel(
            networkResponse = Result.success(expectedDetails),
            testScheduler = testScheduler
        )

        val turbine = viewModel.uiState.testIn(backgroundScope)

        assertEquals(DetailUiState.Loading, turbine.awaitItem())
        assertEquals(DetailUiState.Loaded(expectedDetails), turbine.awaitItem())

        turbine.ensureAllEventsConsumed()
    }

    @Test
    fun showsFailureWhenTheRequestFails() = runTestTurbine {
        val expectedErrorMessage = "this is a test failure"

        val viewModel = createViewModel(
            networkResponse = Result.failure(FlickrException("", expectedErrorMessage)),
            testScheduler = testScheduler
        )

        val turbine = viewModel.uiState.testIn(backgroundScope)

        assertEquals(DetailUiState.Loading, turbine.awaitItem())
        assert(turbine.awaitItem() is DetailUiState.Error) { "This Flow emission is expected to be a DetailUiState.Error" }

        turbine.ensureAllEventsConsumed()
    }

    private fun createViewModel(
        networkResponse: Result<PhotoDetails>,
        testScheduler: TestCoroutineScheduler
    ): DetailViewModel {
        val fakeService = FakeFlickrService(getByIdResult = networkResponse)

        return DetailViewModel(
            flickrService = fakeService,
            ioDispatcher = StandardTestDispatcher(testScheduler),
            savedStateHandle = SavedStateHandle(Detail("test-1324", "test title"))
        )
    }
}