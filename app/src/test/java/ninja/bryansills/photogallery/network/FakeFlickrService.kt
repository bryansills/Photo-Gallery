package ninja.bryansills.photogallery.network

import kotlin.time.Instant

class FakeFlickrService(
    var interestingResult: Result<List<GalleryItem>> = Result.success(listOf()),
    var searchResult: Result<List<GalleryItem>> = Result.success(listOf()),
    var getByIdResult: Result<PhotoDetails> = Result.success(FakePhotoDetails),
) : FlickrService {
    override suspend fun getInteresting(): Result<List<GalleryItem>> {
        return interestingResult
    }

    override suspend fun search(query: String): Result<List<GalleryItem>> {
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