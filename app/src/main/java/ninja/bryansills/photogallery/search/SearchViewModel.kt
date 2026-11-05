package ninja.bryansills.photogallery.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flatMapLatest
import ninja.bryansills.photogallery.di.Dispatch
import ninja.bryansills.photogallery.di.Dispatcher
import ninja.bryansills.photogallery.di.ViewModelSpecific
import ninja.bryansills.photogallery.flickr.GalleryItemPagingSource
import ninja.bryansills.photogallery.network.PhotoRepository
import javax.inject.Inject

@AssistedInject
class SearchViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    @Dispatcher(Dispatch.Io) private val ioDispatcher: CoroutineDispatcher,
    @ViewModelSpecific vmScope: CoroutineScope,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel(vmScope) {
    var searchText by savedStateHandle.saveable { mutableStateOf("") }

    var lastSearch by savedStateHandle.saveable { mutableStateOf("") }

    val galleryItems = snapshotFlow { lastSearch }
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE)
            ) {
                GalleryItemPagingSource(
                    photoRepository = photoRepository,
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

    @AssistedFactory
    @ViewModelAssistedFactoryKey(SearchViewModel::class)
    @ContributesIntoMap(AppScope::class)
    interface Factory : ViewModelAssistedFactory {
        override fun create(extras: CreationExtras): ViewModel {
            return create(extras.createSavedStateHandle())
        }

        fun create(@Assisted savedStateHandle: SavedStateHandle): SearchViewModel
    }
}
