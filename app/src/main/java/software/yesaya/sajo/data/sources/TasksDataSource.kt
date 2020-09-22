package software.yesaya.sajo.data.sources

import androidx.lifecycle.LiveData
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.network.TokenManager

/**
 * Main entry point for accessing tasks data.
 */
interface TasksDataSource {

    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun getTasks(service: ApiService): Result<List<Task>>

    suspend fun refreshTasks(service: ApiService)

    fun observeTask(taskId: Int): LiveData<Result<Task>>

    suspend fun getTask(service: ApiService,taskId: Int): Result<Task>

    suspend fun refreshTask(taskId: Int)

    suspend fun saveTask(service: ApiService, task: Task)

    suspend fun completeTask(service: ApiService, task: Task)

    suspend fun completeTask(service: ApiService, taskId: Int)

    suspend fun activateTask(service: ApiService, task: Task)

    suspend fun activateTask(service: ApiService, taskId: Int)

    suspend fun clearCompletedTasks(service: ApiService)

    suspend fun deleteAllTasks(service: ApiService)

    suspend fun deleteTask(service: ApiService, taskId: Int)
}