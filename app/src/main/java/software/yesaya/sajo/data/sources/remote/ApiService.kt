package software.yesaya.sajo.data.sources.remote

import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.*
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.data.sources.remote.network.AccessToken

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    fun register(@Field("name") name: String, @Field("email") email: String, @Field("password") password: String): Call<AccessToken>

    @POST("login")
    @FormUrlEncoded
    fun login(@Field("email") username: String, @Field("password") password: String): Call<AccessToken>

    @POST("refresh")
    @FormUrlEncoded
    fun refresh(@Field("refresh_token") refreshToken: String): Call<AccessToken>

    @POST("logout")
    fun logout(): Call<AccessToken>

    @GET("tasks")
    fun getTasks(): Deferred<List<Task>>

    @POST("tasks/completeTask/{taskId}")
    fun completeTask(@Path("taskId") taskId: Int): Call<Task>

    @POST("tasks/activateTask/{taskId}")
    fun activateTask(@Path("taskId") taskId: Int): Call<Task>

    @POST("tasks/getTask/{taskId}")
    fun getTask(@Path("taskId") taskId: Int): Call<Task>

    @POST("tasks/saveTask")
    @FormUrlEncoded
    fun saveTask(
        @Field("id") id: Int,
        @Field("title") title: String,
        @Field("description") description: String
    ): Call<Task>

    @DELETE("tasks/deleteTask/{taskId}")
    fun deleteTask(@Path("taskId") taskId: Int): Call<Int>

    @DELETE("tasks/clearCompletedTasks")
    fun clearCompletedTasks(): Call<Int>

    @DELETE("tasks/deleteAllTasks")
    fun deleteAllTasks(): Call<Int>
}