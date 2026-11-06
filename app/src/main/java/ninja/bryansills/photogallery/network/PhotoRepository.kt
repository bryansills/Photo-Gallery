package ninja.bryansills.photogallery.network

import ninja.bryansills.photogallery.data.GalleryItem
import ninja.bryansills.photogallery.data.PhotoDetails

interface PhotoRepository {
    /**
     * @param page The page number for the results (zero-indexed)
     * @param pageSize The number of results you expect
     */
    suspend fun getPhotos(page: Int, pageSize: Int): Result<List<GalleryItem>>

    /**
     * @param page The page number for the results (zero-indexed)
     * @param pageSize The number of results you expect
     */
    suspend fun search(query: String, page: Int, pageSize: Int): Result<List<GalleryItem>>

    suspend fun getPhotoById(id: String): Result<PhotoDetails>
}