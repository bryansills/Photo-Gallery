package ninja.bryansills.photogallery.data

data class GalleryItem(
    val id: String,
    val title: String?,
    val url: String,
    val description: String?,
    val query: String,
)
