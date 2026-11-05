package ninja.bryansills.photogallery.data

import kotlin.time.Instant

data class PhotoDetails(
    val id: String,
    val title: String?,
    val url: String,
    val description: String?,
    val dateTaken: Instant?,
    val datePosted: Instant?
)
