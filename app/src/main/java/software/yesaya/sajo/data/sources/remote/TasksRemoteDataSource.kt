package software.yesaya.sajo.data.sources.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import retrofit2.await
import software.yesaya.sajo.data.sources.Result
import software.yesaya.sajo.data.sources.Result.Success
import software.yesaya.sajo.data.sources.Result.Error
import software.yesaya.sajo.data.sources.TasksDataSource
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.data.sources.remote.network.Network
import software.yesaya.sajo.data.sources.remote.network.TokenManager

/**
 * Implementation of the data source that adds a latency simulating network.
 */
object TasksRemoteDataSource : TasksDataSource {

    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return observableTasks
    }

    override suspend fun getTasks(service: ApiService): Result<List<Task>> {

        val tasks = service.getTasks().await()

        return try {
            Success(tasks)
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun refreshTasks(service: ApiService) {
        observableTasks.value = getTasks(service)
    }

    override fun observeTask(taskId: Int): LiveData<Result<Task>> {
        return observableTasks.map { tasks ->
            when (tasks) {
                is Result.Loading -> Result.Loading
                is Error -> Error(tasks.exception)
                is Success -> {
                    val task = tasks.data.firstOrNull() { it.id == taskId }
                        ?: return@map Error(Exception("Not found"))
                    Success(task)
                }
            }
        }
    }

    override suspend fun getTask(service: ApiService, taskId: Int): Result<Task> {
        val task = service.getTask(taskId).await()

        return Success(task)
    }

    override suspend fun refreshTask(taskId: Int) {
//        refreshTasks()
    }

    override suspend fun saveTask(service: ApiService, task: Task) {
        service.saveTask(task.id, task.title, task.description).await()
    }

    override suspend fun completeTask(service: ApiService, task: Task) {
        service.completeTask(task.id).await()
    }

    override suspend fun completeTask(service: ApiService, taskId: Int) {
        service.completeTask(taskId).await()
    }

    override suspend fun activateTask(service: ApiService, task: Task) {
        service.activateTask(task.id).await()
    }

    override suspend fun activateTask(service: ApiService, taskId: Int) {
        service.activateTask(taskId).await()
    }

    override suspend fun clearCompletedTasks(service: ApiService) {
        service.clearCompletedTasks().await()
    }

    override suspend fun deleteAllTasks(service: ApiService) {
        service.deleteAllTasks().await()
    }

    override suspend fun deleteTask(service: ApiService, taskId: Int) {
        try {
            service.deleteTask(taskId).await()
        } catch (e: Exception) {
            Error(e)
        }
    }

}