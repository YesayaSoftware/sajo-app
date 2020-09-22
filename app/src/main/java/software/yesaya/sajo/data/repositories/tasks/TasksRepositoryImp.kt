package software.yesaya.sajo.data.repositories.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import software.yesaya.sajo.data.sources.TasksDataSource
import software.yesaya.sajo.data.sources.Result
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.network.Network
import software.yesaya.sajo.data.sources.remote.network.TokenManager

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 */
class TasksRepositoryImp(
    private val tokenManager: TokenManager,
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksRepository {
    override fun service(): ApiService {
        return  Network.createServiceWithAuth(ApiService::class.java, tokenManager)
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksLocalDataSource.observeTasks()
    }

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (forceUpdate) {
            try {
                updateTasksFromRemoteDataSource()
            } catch (ex: Exception) {
                return Result.Error(ex)
            }
        }
        return tasksLocalDataSource.getTasks(service())
    }

    override suspend fun refreshTasks() {
        updateTasksFromRemoteDataSource()
    }

    override fun observeTask(taskId: Int): LiveData<Result<Task>> {
        return tasksLocalDataSource.observeTask(taskId)
    }

    override suspend fun getTask(taskId: Int, forceUpdate: Boolean): Result<Task> {
        if (forceUpdate) {
            updateTaskFromRemoteDataSource(taskId)
        }

        return tasksLocalDataSource.getTask(service(), taskId)
    }

    override suspend fun refreshTask(taskId: Int) {
        updateTaskFromRemoteDataSource(taskId)
    }

    override suspend fun completeTask(taskId: Int) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Result.Success)?.let { task ->
                completeTask(task.data)
            }
        }
    }

    override suspend fun activateTask(taskId: Int) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Result.Success)?.let { task ->
                activateTask(task.data)
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { tasksRemoteDataSource.clearCompletedTasks(service()) }
            launch { tasksLocalDataSource.clearCompletedTasks(service()) }
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteAllTasks(service()) }
                launch { tasksLocalDataSource.deleteAllTasks(service()) }
            }
        }
    }

    override suspend fun deleteTask(taskId: Int) {
        coroutineScope {
            launch { tasksRemoteDataSource.deleteTask(service(), taskId) }
            launch { tasksLocalDataSource.deleteTask(service(), taskId) }
        }
    }

    override suspend fun activateTask(task: Task) = withContext<Unit>(ioDispatcher) {
        coroutineScope {
            launch { tasksRemoteDataSource.activateTask(service(), task) }
            launch { tasksLocalDataSource.activateTask(service(), task) }
        }
    }

    override suspend fun completeTask(task: Task) {
        coroutineScope {
            launch { tasksRemoteDataSource.completeTask(service(), task) }
            launch { tasksLocalDataSource.completeTask(service(), task) }
        }
    }

    override suspend fun saveTask(task: Task) {
        coroutineScope {
            launch { tasksRemoteDataSource.saveTask(service(), task) }
            launch { tasksLocalDataSource.saveTask(service(), task) }
        }
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        val remoteTasks = tasksRemoteDataSource.getTasks(service())

        if (remoteTasks is Result.Success) {
            // Real apps might want to do a proper sync.
            tasksLocalDataSource.deleteAllTasks(service())
            remoteTasks.data.forEach { task ->
                tasksLocalDataSource.saveTask(service(), task)
            }
        } else if (remoteTasks is Result.Error) {
            throw remoteTasks.exception
        }
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: Int) {
        val remoteTask = tasksRemoteDataSource.getTask(service(), taskId)

        if (remoteTask is Result.Success) {
            tasksLocalDataSource.saveTask(service(), remoteTask.data)
        }
    }

    private suspend fun getTaskWithId(id: Int): Result<Task> {
        return tasksLocalDataSource.getTask(service(), id)
    }
}