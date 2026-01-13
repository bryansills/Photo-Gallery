package ninja.bryansills.photogallery.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flatMapLatest
import ninja.bryansills.photogallery.di.Dispatch
import ninja.bryansills.photogallery.di.Dispatcher
import ninja.bryansills.photogallery.network.FlickrService
import ninja.bryansills.photogallery.paging.FlickrPagingSource
import se.ansman.dagger.auto.androidx.viewmodel.ViewModelSpecific
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val flickrService: FlickrService,
    @Dispatcher(Dispatch.Io) private val ioDispatcher: CoroutineDispatcher,
    @ViewModelSpecific vmScope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var searchText by savedStateHandle.saveable { mutableStateOf("") }

    var lastSearch by savedStateHandle.saveable { mutableStateOf("") }

    val galleryItems = snapshotFlow { lastSearch }
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE)
            ) {
                FlickrPagingSource(
                    flickrService = flickrService,
                    ioDispatcher = ioDispatcher,
                    query = query,
                    pageSize = PAGE_SIZE
                )
            }
                .flow
                .cachedIn(vmScope)
        }
        .cachedIn(vmScope)

    fun search() {
        // needed for unit tests where the ViewModel does not exist inside a Snapshot
        Snapshot.withMutableSnapshot {
            lastSearch = searchText
        }
    }

    companion object {
        const val PAGE_SIZE = 50
    }
}
