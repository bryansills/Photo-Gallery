package ninja.bryansills.photogallery.network

import ninja.bryansills.photogallery.data.GalleryItem
import ninja.bryansills.photogallery.data.PhotoDetails
import kotlin.time.Instant

class FakePhotoRepository(
    var interestingResult: Result<List<GalleryItem>> = Result.success(listOf()),
    var searchResult: Result<List<GalleryItem>> = Result.success(listOf()),
    var getByIdResult: Result<PhotoDetails> = Result.success(FakePhotoDetails),
) : PhotoRepository {
    override suspend fun getPhotos(page: Int, pageSize: Int): Result<List<GalleryItem>> {
        return interestingResult
    }

    override suspend fun search(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<GalleryItem>> {
        return searchResult
    }

    override suspend fun getPhotoById(id: String): Result<PhotoDetails> {
        return getByIdResult
    }

    companion object {
        val FakePhotoDetails = PhotoDetails(
            id = "test-1234",
            title = "test title",
            url = "https://fake.com/cool.jpg",
            description = "test description",
            dateTaken = Instant.DISTANT_PAST,
            datePosted = Instant.DISTANT_FUTURE
        )
    }
}