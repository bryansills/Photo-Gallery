package ninja.bryansills.photogallery

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalResources

sealed interface DisplayText {
    data class Raw(val text: String) : DisplayText
    data class ResourceId(@StringRes val resId: Int) : DisplayText
    data class ResourceIdArgs(@StringRes val resId: Int, val args: List<Any?>) : DisplayText
}

@Composable
fun DisplayText.resolve(resources: Resources = LocalResources.current): String {
        return when (this) {
            is DisplayText.Raw -> this.text
            is DisplayText.ResourceId -> resources.getString(this.resId)
            is DisplayText.ResourceIdArgs -> resources.getString(this.resId, *args.toTypedArray())
        }
    }