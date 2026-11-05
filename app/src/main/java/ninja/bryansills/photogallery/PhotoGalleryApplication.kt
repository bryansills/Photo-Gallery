package ninja.bryansills.photogallery

import android.app.Application
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class PhotoGalleryApplication : Application(), MetroApplication {
    private lateinit var androidAppGraph: AndroidAppGraph

    override val appComponentProviders: MetroAppComponentProviders
        get() = androidAppGraph

    override fun onCreate() {
        super.onCreate()
        androidAppGraph = createGraph<AndroidAppGraph>()
    }
}