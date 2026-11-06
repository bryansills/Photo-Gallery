package ninja.bryansills.photogallery.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


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
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
}
