package ninja.bryansills.photogallery.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ninja.bryansills.photogallery.DisplayText
import ninja.bryansills.photogallery.R
import ninja.bryansills.photogallery.di.Dispatch
import ninja.bryansills.photogallery.di.Dispatcher
import ninja.bryansills.photogallery.network.FlickrService
import ninja.bryansills.photogallery.network.GalleryItem
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val flickrService: FlickrService,
    @Dispatcher(Dispatch.Io) private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var searchText by savedStateHandle.saveable { mutableStateOf("") }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    var networkJob: Job? = null

    init {
        search()
    }

    fun search() {
        networkJob?.cancel()
        networkJob = viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isWorking = true, error = null) }

            try {
                val query = searchText
                val searchResult = if (query.isEmpty()) {
                    flickrService.getInteresting()
                } else {
                    flickrService.search(searchText)
                }

                searchResult.fold(
                    onSuccess = { success ->
                        _uiState.update {
                            SearchUiState(
                                searchedTerm = query,
                                isWorking = false,
                                error = null,
                                items = success
                            )
                        }
                    },
                    onFailure = { failure ->
                        _uiState.update {
                            SearchUiState(
                                isWorking = false,
                                error = DisplayText.ResourceIdArgs(R.string.network_error, listOf(failure.message))
                            )
                        }
                    }
                )
            } catch (ex: Exception) {
                _uiState.update { old ->
                    old.copy(
                        isWorking = false,
                        error = DisplayText.ResourceIdArgs(R.string.unexpected_error, listOf(ex.message))
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkJob?.cancel()
        networkJob = null
    }
}

data class SearchUiState(
    val searchedTerm: String = "",
    val isWorking: Boolean = false,
    val items: List<GalleryItem> = listOf(),
    val error: DisplayText? = null
)