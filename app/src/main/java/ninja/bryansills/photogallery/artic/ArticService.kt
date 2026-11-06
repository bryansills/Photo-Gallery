package ninja.bryansills.photogallery.artic

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ninja.bryansills.photogallery.data.GalleryItem
import ninja.bryansills.photogallery.data.PhotoDetails
import ninja.bryansills.photogallery.di.Dispatch
import ninja.bryansills.photogallery.di.Dispatcher
import ninja.bryansills.photogallery.network.PhotoRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.Executors
import okhttp3.Dispatcher as OkHttpDispatcher

/**
 * A simple Retrofit API to query the Art Institute of Chicago API
 * https://api.artic.edu/docs
 */
interface ArticService {
    @GET("artworks?fields=id,title,image_id,alt_titles")
    suspend fun getArtworks(@Query("page") page: Int, @Query("limit") pageSize: Int): GetArtworksResponse

    @GET("artworks/search?fields=id,title,image_id,alt_titles")
    suspend fun searchArtworks(@Query("q") query: String, @Query("page") page: Int, @Query("limit") pageSize: Int): GetArtworksResponse

    @GET("artworks/{id}?fields=id,title,image_id,alt_titles")
    suspend fun getArtworkById(@Path("id") artworkId: String): GetArtworkByIdResponse
}

@ContributesBinding(AppScope::class)
@Inject
class ArticPhotoRepository(
    private val articService: ArticService,
    @Dispatcher(Dispatch.Io) private val dispatcher: CoroutineDispatcher,
) : PhotoRepository {
    override suspend fun getPhotos(
        page: Int,
        pageSize: Int
    ): Result<List<GalleryItem>> {
        return withContext(dispatcher) {
            try {
                val response = articService.getArtworks(
                    page = page + 1, // artic API is 1-indexed
                    pageSize = pageSize,
                )
                Result.success(response.toPhotoDetails(query = ""))
            } catch (ex: Throwable) {
                ensureActive()
                Result.failure(ex)
            }
        }
    }

    override suspend fun search(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<GalleryItem>> {
        return withContext(dispatcher) {
            try {
                val response = articService.searchArtworks(
                    query = query, 
                    page = page + 1, // artic API is 1-indexed
                    pageSize = pageSize,
                )
                Result.success(response.toPhotoDetails(query = query))
            } catch (ex: Throwable) {
                ensureActive()
                Result.failure(ex)
            }
        }
    }

    override suspend fun getPhotoById(id: String): Result<PhotoDetails> {
        return withContext(dispatcher) {
            try {
                val response = articService.getArtworkById(id)
                Result.success(response.toPhotoDetails())
            } catch (ex: Throwable) {
                ensureActive()
                Result.failure(ex)
            }
        }
    }
}

private fun GetArtworksResponse.toPhotoDetails(query: String): List<GalleryItem> {
    val baseImgUrl = this.config.iiif_url
    return this.data.map { artwork ->
        GalleryItem(
            id = artwork.id.toString(),
            title = artwork.title,
            url = "$baseImgUrl/${artwork.image_id}/full/843,/0/default.jpg",
            description = artwork.alt_titles?.firstOrNull(),
            query = query
        )
    }
}

private fun GetArtworkByIdResponse.toPhotoDetails(): PhotoDetails {
    val baseImgUrl = this.config.iiif_url
    return PhotoDetails(
        id = this.data.id.toString(),
        title = this.data.title,
        url = "$baseImgUrl/${this.data.image_id}/full/843,/0/default.jpg",
        description = this.data.alt_titles?.firstOrNull(),
        dateTaken = null,
        datePosted = null
    )
}

@ContributesTo(AppScope::class)
@BindingContainer
object ArticModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(CourtesyHeaderInterceptor())
        .build()

    @Provides
    @SingleIn(AppScope::class)
    fun provideArticService(okHttpClient: OkHttpClient): ArticService {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.artic.edu/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json; charset=UTF8".toMediaType())
            )
            .build()
        return retrofit.create()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideImageLoader(applicationContext: Context, okHttpClient: OkHttpClient): ImageLoader {
        val okHttpDispatcher = OkHttpDispatcher(Executors.newSingleThreadExecutor())
        val singleThreadOkHttp = okHttpClient.newBuilder()
            .dispatcher(okHttpDispatcher)
            .addInterceptor(CourtesyDelayInterceptor())
            .build()
        return ImageLoader.Builder(applicationContext)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = singleThreadOkHttp))
            }
            .build()
    }
}