package ninja.bryansills.photogallery.data

import com.googlecode.flickrjandroid.photos.PhotoList

data class GalleryItem(
    val id: String,
    val title: String?,
    val url: String,
    val description: String?,
    val query: String,
)
