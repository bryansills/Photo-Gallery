package ninja.bryansills.photogallery.artic

/**
 * A simple Retrofit API to query the Art Institute of Chicago API
 * https://api.artic.edu/docs
 */
interface ArticService {
    suspend fun getArtworks(page: Int, pageSize: Int)

    suspend fun searchArtworks(query: String, page: Int, pageSize: Int)

    suspend fun getArtworkById(artworkId: String)
}

