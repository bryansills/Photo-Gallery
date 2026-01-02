package ninja.bryansills.photogallery.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ninja.bryansills.photogallery.network.FlickrService
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val flickrService: FlickrService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()