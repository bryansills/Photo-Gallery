package ninja.bryansills.photogallery.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ninja.bryansills.photogallery.network.FlickrService
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val flickrService: FlickrService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    init {
        viewModelScope.launch {
            val result = flickrService.getInteresting()
            result.fold(
                onSuccess = {
                    Log.d("BLARG", "success! $it")
                },
                onFailure = {
                    Log.d("BLARG", "failure :( $it")
                }
            )
        }
    }
}