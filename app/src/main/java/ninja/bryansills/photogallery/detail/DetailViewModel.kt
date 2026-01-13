package ninja.bryansills.photogallery.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import ninja.bryansills.photogallery.DisplayText
import ninja.bryansills.photogallery.R
import ninja.bryansills.photogallery.di.Dispatch
import ninja.bryansills.photogallery.di.Dispatcher
import ninja.bryansills.photogallery.network.FlickrService
import ninja.bryansills.photogallery.network.PhotoDetails
import se.ansman.dagger.auto.androidx.viewmodel.ViewModelSpecific
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val flickrService: FlickrService,
    @Dispatcher(Dispatch.Io) private val ioDispatcher: CoroutineDispatcher,
    @ViewModelSpecific vmScope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route: Detail = savedStateHandle.toRoute()

    val uiState: StateFlow<DetailUiState> = flow {
        val uiState = try {
            val detailResult = flickrService.getPhotoById(route.id)
            detailResult.fold(
                onSuccess = { success ->
                    DetailUiState.Loaded(success)
                },
                onFailure = { failure ->
                    DetailUiState.Error(
                        DisplayText.ResourceIdArgs(R.string.unexpected_error, listOf(failure.message))
                    )
                }
            )
        } catch (ex: Exception) {
            DetailUiState.Error(
                DisplayText.ResourceIdArgs(R.string.unexpected_error, listOf(ex.message))
            )
        }

        emit(uiState)
    }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = vmScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DetailUiState.Loading
        )
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Loaded(val photoDetails: PhotoDetails) : DetailUiState
    data class Error(val message: DisplayText) : DetailUiState
}