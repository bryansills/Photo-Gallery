package ninja.bryansills.photogallery.network

import com.googlecode.flickrjandroid.Flickr
import com.googlecode.flickrjandroid.FlickrException
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
}

class DefaultFlickrService @Inject constructor(
    private val flickr: Flickr,
    @Dispatcher(Dispatch.Io) private val dispatcher: CoroutineDispatcher
) : FlickrService {
    override suspend fun getInteresting(): Result<List<GalleryItem>> {
        return withContext(dispatcher) {
            try {
                val response = flickr.interestingnessInterface.list
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
