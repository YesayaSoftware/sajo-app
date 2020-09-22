package software.yesaya.sajo.utils

import okhttp3.ResponseBody
import retrofit2.Converter
import software.yesaya.sajo.data.sources.remote.network.ApiError
import software.yesaya.sajo.data.sources.remote.network.ApiErrorException
import software.yesaya.sajo.data.sources.remote.network.Network

private val PUNCTUATION = listOf(", ", "; ", ": ", " ")

fun String.smartTruncate(length: Int): String {
    val words = split(" ")
    var added = 0
    var hasMore = false
    val builder = StringBuilder()
    for (word in words) {
        if (builder.length > length) {
            hasMore = true
            break
        }
        builder.append(word)
        builder.append(" ")
        added += 1
    }

    PUNCTUATION.map {
        if (builder.endsWith(it)) {
            builder.replace(builder.length - it.length, builder.length, "")
        }
    }

    if (hasMore) {
        builder.append("...")
    }
    return builder.toString()
}

object Utils {
    fun convertErrors(response: ResponseBody): ApiError? {
        val converter : Converter<ResponseBody, ApiError> =
            Network.getRetrofits().responseBodyConverter(ApiError::class.java, arrayOfNulls<Annotation>(0))

        var apiError: ApiError? = null

        try {
            apiError = converter.convert(response)
        } catch (e: ApiErrorException) {
            e.printStackTrace()
        }

        return apiError
    }
}