package ninja.bryansills.photogallery.network

import com.googlecode.flickrjandroid.Flickr
import com.googlecode.flickrjandroid.FlickrException
import com.googlecode.flickrjandroid.photos.SearchParameters
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ninja.bryansills.photogallery.di.Dispatch
import ninja.bryansills.photogallery.di.Dispatcher
import org.json.JSONException
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.inject.Inject
import javax.inject.Singleton

interface FlickrService {
    suspend fun getInteresting(): Result<List<GalleryItem>>

    suspend fun search(query: String): Result<List<GalleryItem>>

    suspend fun getPhotoById(id: String): Result<PhotoDetails>
}

class DefaultFlickrService @Inject constructor(
    private val flickr: Flickr,
    @Dispatcher(Dispatch.Io) private val dispatcher: CoroutineDispatcher
) : FlickrService {
    override suspend fun getInteresting(): Result<List<GalleryItem>> {
        return withContext(dispatcher) {
            try {
                val response = FlickrCompat.getList(
                    flickr.interestingnessInterface, null, null, SEARCH_PAGE_SIZE, 0
                )
                Result.success(response.toGalleryItems())
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

    override suspend fun search(query: String): Result<List<GalleryItem>> {
        return withContext(dispatcher) {
            try {
                val searchParams = SearchParameters().apply { text = query }
                val response = flickr.photosInterface.search(searchParams, SEARCH_PAGE_SIZE, 0)
                Result.success(response.toGalleryItems())
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

    companion object {
        const val SEARCH_PAGE_SIZE = 50
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

@Module
@InstallIn(SingletonComponent::class)
internal interface FlickrModule {
    @Binds
    @Singleton
    fun bindFlickrService(impl: DefaultFlickrService): FlickrService

    companion object {
        @Provides
        @Singleton
        fun provideFlickr(): Flickr {
            // lots of ways to do this better/safer, but not worth implementing now
            return Flickr("add in your API key here")
        }
    }
}
