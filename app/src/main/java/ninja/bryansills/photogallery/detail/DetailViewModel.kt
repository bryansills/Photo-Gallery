package ninja.bryansills.photogallery.detail

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import ninja.bryansills.photogallery.DisplayText
import ninja.bryansills.photogallery.R
import ninja.bryansills.photogallery.data.PhotoDetails
import ninja.bryansills.photogallery.di.Dispatch
import ninja.bryansills.photogallery.di.Dispatcher
import ninja.bryansills.photogallery.di.ViewModelSpecific
import ninja.bryansills.photogallery.network.PhotoRepository

@AssistedInject
class DetailViewModel(
    private val photoRepository: PhotoRepository,
    @Dispatcher(Dispatch.Io) private val ioDispatcher: CoroutineDispatcher,
    @ViewModelSpecific vmScope: CoroutineScope,
    @Assisted private val route: Detail
) : ViewModel(vmScope) {
    val uiState: StateFlow<DetailUiState> = flow {
        val uiState = try {
            val detailResult = photoRepository.getPhotoById(route.id)
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
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(@Assisted route: Detail): DetailViewModel
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Loaded(val photoDetails: PhotoDetails) : DetailUiState
    data class Error(val message: DisplayText) : DetailUiState
}