package ninja.bryansills.photogallery.network

import com.googlecode.flickrjandroid.Flickr
import com.googlecode.flickrjandroid.FlickrException
import com.googlecode.flickrjandroid.photos.SearchParameters
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ninja.bryansills.photogallery.di.Dispatch
import ninja.bryansills.photogallery.di.Dispatcher
import org.json.JSONException
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

interface FlickrService {
    suspend fun getInteresting(page: Int, pageSize: Int): Result<List<GalleryItem>>

    suspend fun search(query: String, page: Int, pageSize: Int): Result<List<GalleryItem>>

    suspend fun getPhotoById(id: String): Result<PhotoDetails>
}

@ContributesBinding(AppScope::class)
@Inject
class DefaultFlickrService(
    private val flickr: Flickr,
    @Dispatcher(Dispatch.Io) private val dispatcher: CoroutineDispatcher
) : FlickrService {
    override suspend fun getInteresting(page: Int, pageSize: Int): Result<List<GalleryItem>> {
        return withContext(dispatcher) {
            try {
                val response = FlickrCompat.getList(
                    flickr.interestingnessInterface, null, null, pageSize, page
                )
                val galleryItems = response.toGalleryItems(query = "")
                Result.success(galleryItems)
            } catch (ex: Throwable) {
                if (ex.isLibraryException) {
                    Result.failure(ex)
                } else {
                    // don't want to accidentally catch exs that we shouldn't
                    throw ex
                }
            }
        }
    }

    override suspend fun search(query: String, page: Int, pageSize: Int): Result<List<GalleryItem>> {
        return withContext(dispatcher) {
            try {
                val searchParams = SearchParameters().apply { text = query }
                val response = flickr.photosInterface.search(searchParams, pageSize, page)
                val galleryItems = response.toGalleryItems(query = query)
                Result.success(galleryItems)
            } catch (ex: Throwable) {
                if (ex.isLibraryException) {
                    Result.failure(ex)
                } else {
                    // don't want to accidentally catch exs that we shouldn't
                    throw ex
                }
            }
        }
    }

    override suspend fun getPhotoById(id: String): Result<PhotoDetails> {
        return withContext(dispatcher) {
            try {
                val response = flickr.photosInterface.getPhoto(id)
                Result.success(response.toPhotoDetails())
            } catch (ex: Throwable) {
                if (ex.isLibraryException) {
                    Result.failure(ex)
                } else {
                    // don't want to accidentally catch exs that we shouldn't
                    throw ex
                }
            }
        }
    }
}

private val Throwable.isLibraryException: Boolean
    get() {
        return this is FlickrException
                || this is IOException
                || this is JSONException
                || this is NoSuchAlgorithmException
                || this is InvalidKeyException
}

@ContributesTo(AppScope::class)
interface FlickrModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideFlickr(): Flickr {
        // lots of ways to do this better/safer, but not worth implementing now
        return Flickr("add in your API key here")
    }
}
