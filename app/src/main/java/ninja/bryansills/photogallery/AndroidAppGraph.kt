package ninja.bryansills.photogallery

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : MetroAppComponentProviders, ViewModelGraph {
    @Provides fun provideApplicationContext(application: Application): Context = application

    val imageLoader: ImageLoader

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides application: Application,
        ): AndroidAppGraph
    }
}
