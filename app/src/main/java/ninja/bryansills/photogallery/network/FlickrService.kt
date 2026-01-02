package ninja.bryansills.photogallery.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface FlickrService {
}

class DefaultFlickrService @Inject constructor() : FlickrService

@Module
@InstallIn(SingletonComponent::class)
internal object FlickrModule {
    @Provides
    @Singleton
    fun provideFlickrService(): FlickrService {
        return DefaultFlickrService()
    }
}