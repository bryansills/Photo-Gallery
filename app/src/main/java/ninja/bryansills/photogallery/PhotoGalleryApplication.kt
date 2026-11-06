package ninja.bryansills.photogallery

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class PhotoGalleryApplication : Application(), MetroApplication, SingletonImageLoader.Factory {
    private lateinit var androidAppGraph: AndroidAppGraph

    override val appComponentProviders: MetroAppComponentProviders
        get() = androidAppGraph

    override fun onCreate() {
        super.onCreate()
        androidAppGraph = createGraphFactory<AndroidAppGraph.Factory>().create(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return androidAppGraph.imageLoader
    }
}