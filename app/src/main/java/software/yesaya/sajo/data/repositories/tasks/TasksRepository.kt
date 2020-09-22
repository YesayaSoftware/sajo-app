package software.yesaya.sajo.data.repositories.tasks

import androidx.lifecycle.LiveData
import software.yesaya.sajo.data.sources.Result
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.network.Network
import software.yesaya.sajo.data.sources.remote.network.TokenManager

/**
 * Interface to the data layer.
 */
interface TasksRepository {
    fun service(): ApiService

    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>

    suspend fun refreshTasks()

    fun observeTask(taskId: Int): LiveData<Result<Task>>

    suspend fun getTask(taskId: Int, forceUpdate: Boolean = false): Result<Task>

    suspend fun refreshTask(taskId: Int)

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: Int)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: Int)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: Int)
}