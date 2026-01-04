package ninja.bryansills.photogallery.network

import kotlin.time.Instant

class FakePageableFlickrService(
    var getByIdResult: Result<PhotoDetails> = Result.success(FakePhotoDetails),
) : FlickrService {
    override suspend fun getInteresting(page: Int, pageSize: Int): Result<List<GalleryItem>> {
        val offset = page * pageSize
        val items = (0 until pageSize).map { index ->
            createTestGalleryItem(
                id = index + offset,
                query = ""
            )
        }
        return Result.success(items)
    }

    override suspend fun search(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<GalleryItem>> {
        val offset = page * pageSize
        val items = (0 until pageSize).map { index ->
            createTestGalleryItem(
                id = index + offset,
                query = query
            )
        }
        return Result.success(items)
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

fun createTestGalleryItem(id: Int = 0, query: String): GalleryItem {
    return GalleryItem(
        id = "test-id-$id",
        title = "test title $id",
        url = "https://fake.com/cool-$id.jpg",
        description = "test description $id",
        query = query
    )
}