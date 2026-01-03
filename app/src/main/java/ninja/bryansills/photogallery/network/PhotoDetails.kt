package ninja.bryansills.photogallery.network

import com.googlecode.flickrjandroid.photos.Photo
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

data class PhotoDetails(
    val id: String,
    val title: String?,
    val url: String,
    val description: String?,
    val dateTaken: Instant?,
    val datePosted: Instant?
)

fun Photo.toPhotoDetails(): PhotoDetails {
    return PhotoDetails(
        id = this.id,
        title = this.title,
        url = this.originalUrl,
        description = this.description,
        dateTaken = this.dateTaken?.toInstant()?.toKotlinInstant(),
        datePosted = this.datePosted?.toInstant()?.toKotlinInstant(),
    )
}