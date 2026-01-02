package ninja.bryansills.photogallery.network

import com.googlecode.flickrjandroid.photos.PhotoList

data class GalleryItem(
    val id: String,
    val url: String,
)

fun PhotoList.toGalleryItems(): List<GalleryItem> {
    return this.map {
        GalleryItem(
            id = it.id,
            url = it.mediumUrl
        )
    }
}