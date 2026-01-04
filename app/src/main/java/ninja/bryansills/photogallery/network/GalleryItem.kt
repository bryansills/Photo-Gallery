package ninja.bryansills.photogallery.network

import com.googlecode.flickrjandroid.photos.PhotoList

data class GalleryItem(
    val id: String,
    val title: String?,
    val url: String,
    val description: String?,
    val query: String,
)

fun PhotoList.toGalleryItems(query: String): List<GalleryItem> {
    return this.map {
        GalleryItem(
            id = it.id,
            title = it.title,
            url = it.mediumUrl,
            description = it.description,
            query = query,
        )
    }
}