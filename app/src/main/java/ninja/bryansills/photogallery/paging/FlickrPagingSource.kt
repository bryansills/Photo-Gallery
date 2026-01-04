package ninja.bryansills.photogallery.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ninja.bryansills.photogallery.network.FlickrService
import ninja.bryansills.photogallery.network.GalleryItem

class FlickrPagingSource(
    private val flickrService: FlickrService,
    private val ioDispatcher: CoroutineDispatcher,
    private val query: String,
    private val pageSize: Int
) : PagingSource<Int, GalleryItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val pageNumber = params.key ?: 0

        val response = withContext(ioDispatcher) {
            if (query.isBlank()) {
                flickrService.getInteresting(pageNumber, pageSize)
            } else {
                flickrService.search(query, pageNumber, pageSize)
            }
        }

        return response.fold(
            onSuccess = { items ->
                val prevKey = if (pageNumber > 0) pageNumber - 1 else null
                val nextKey = if (items.isNotEmpty()) pageNumber + 1 else null
                LoadResult.Page(data = items, prevKey = prevKey, nextKey = nextKey)
            },
            onFailure = { fail ->
                LoadResult.Error(fail)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}