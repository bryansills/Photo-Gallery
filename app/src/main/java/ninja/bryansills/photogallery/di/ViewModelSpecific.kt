package ninja.bryansills.photogallery.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext


/**
 * Riff on code in, but for Metro https://github.com/ansman/auto-dagger
 *
 * You gotta pass the CoroutineScope into the extended `ViewModel` constructor.
 *
 * Basically in the class definition, you need to end with:
 *
 * ```kotlin
 * class MyCoolViewModel(...) : ViewModel(coroutineScope)
 * //                                     ^ important part here
 * ```
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ViewModelSpecific

@ContributesTo(AppScope::class)
@BindingContainer
object ViewModelSpecificBindingContainer {
    @Provides
    @ViewModelSpecific
    fun provideViewModelScope(): CoroutineScope =
        ClearableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
}

private class ClearableCoroutineScope(
    override val coroutineContext: CoroutineContext
) : CoroutineScope, AutoCloseable {
    override fun close() {
        coroutineContext.cancel()
    }
}