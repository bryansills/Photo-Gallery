package ninja.bryansills.photogallery.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
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
import ninja.bryansills.photogallery.di.ViewModelSpecific
import ninja.bryansills.photogallery.network.FlickrService
import ninja.bryansills.photogallery.network.PhotoDetails

@AssistedInject
class DetailViewModel constructor(
    private val flickrService: FlickrService,
    @Dispatcher(Dispatch.Io) private val ioDispatcher: CoroutineDispatcher,
    @ViewModelSpecific vmScope: CoroutineScope,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel(vmScope) {
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

    @AssistedFactory
    interface Factory : ViewModelAssistedFactory {
        override fun create(extras: CreationExtras): ViewModel {
            return create(extras.createSavedStateHandle())
        }

        fun create(@Assisted savedStateHandle: SavedStateHandle): DetailViewModel
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Loaded(val photoDetails: PhotoDetails) : DetailUiState
    data class Error(val message: DisplayText) : DetailUiState
}