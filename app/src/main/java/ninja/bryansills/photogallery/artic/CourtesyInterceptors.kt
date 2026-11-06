package ninja.bryansills.photogallery.artic

import okhttp3.Interceptor
import okhttp3.Response
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Follow the directions here: https://api.artic.edu/docs/#authentication
 */
class CourtesyHeaderInterceptor(
    private val aicUserAgent: String = "Photo Gallery (https://github.com/bryansills/Photo-Gallery)",
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("AIC-User-Agent", aicUserAgent)
            .build()
        return chain.proceed(newRequest)
    }
}

/**
 * Follow the guidelines here: https://api.artic.edu/docs/#scraping-images
 *
 * Use this with a single-threaded dispatcher
 */
class CourtesyDelayInterceptor(private val duration: Duration = 1.seconds) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Thread.sleep(duration.inWholeMilliseconds)
        return chain.proceed(chain.request())
    }
}