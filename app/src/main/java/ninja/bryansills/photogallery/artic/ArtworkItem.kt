package ninja.bryansills.photogallery.artic

import kotlinx.serialization.Serializable

@Serializable
data class ArtworkItem(
    val id: Int,
    val title: String,
    val image_id: String?,
    val alt_titles: List<String>?
)

@Serializable
data class ArticConfig(
    val iiif_url: String,
)

@Serializable
data class GetArtworksResponse(
    val data: List<ArtworkItem>,
    val config: ArticConfig,
)

@Serializable
data class GetArtworkByIdResponse(
    val data: ArtworkItem,
    val config: ArticConfig,
)