package software.yesaya.sajo.data.sources.remote.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import software.yesaya.sajo.BuildConfig
import software.yesaya.sajo.data.sources.remote.ApiService

private const val BASE_URL = "http://10.0.2.2:8000/api/"

/**
 * Main entry point for network access. Call like ` Network.sajo.getTasksAsync()`
 */
object Network {
    private val client =  buildClient()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val sajo = retrofit.create(
        ApiService::class.java
    )

    private fun buildClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request = chain.request()

                val builder = request.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Connection", "close")

                request = builder.build()

                chain.proceed(request)
            }

        if (BuildConfig.DEBUG)
            builder.addNetworkInterceptor(StethoInterceptor())

        return builder.build()
    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    fun getRetrofits(): Retrofit {
        return retrofit
    }

    fun <T> createServiceWithAuth(service: Class<T>, tokenManager: TokenManager): T {
        val newClient =client.newBuilder().addInterceptor { chain ->
            var request = chain.request()

            val builder = request.newBuilder()

            if (tokenManager.token.access_token != null)
                builder.addHeader("Authorization", "Bearer " + tokenManager.token.access_token!!)

            request = builder.build()
            chain.proceed(request)
        }.authenticator(CustomAuthenticator.getInstance(tokenManager)).build()

        val newRetrofit = retrofit.newBuilder().client(newClient).build()

        return newRetrofit.create(service)
    }
}