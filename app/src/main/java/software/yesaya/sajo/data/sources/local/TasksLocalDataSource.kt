package software.yesaya.sajo.data.sources.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import software.yesaya.sajo.data.sources.Result
import software.yesaya.sajo.data.sources.Result.Success
import software.yesaya.sajo.data.sources.Result.Error
import software.yesaya.sajo.data.sources.TasksDataSource
import software.yesaya.sajo.data.sources.local.dao.TaskDao
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.network.TokenManager

/**
 * Concrete implementation of a data source as a db.
 */
class TasksLocalDataSource internal constructor(
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return taskDao.observeTasks().map { tasks ->
            Success(tasks)
        }
    }

    override suspend fun getTasks(service: ApiService): Result<List<Task>>  = withContext(ioDispatcher) {
        return@withContext try {
            Success(taskDao.getTasks())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun refreshTasks(service: ApiService) {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: Int): LiveData<Result<Task>> {
        return taskDao.observeTaskById(taskId).map { task ->
            Success(task)
        }
    }

    override suspend fun getTask(service: ApiService, taskId: Int): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = taskDao.getTaskById(taskId)

            if (task != null) {
                return@withContext Success(task)
            } else {
                return@withContext Error(Exception("Task not found!"))
            }

        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun refreshTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun saveTask(service: ApiService, task: Task) = withContext(ioDispatcher) {
        taskDao.insertTask(task)
    }

    override suspend fun completeTask(service: ApiService, task: Task) = withContext(ioDispatcher) {
        taskDao.updateCompleted(task.id, true)
    }

    override suspend fun completeTask(service: ApiService, taskId: Int) {
        taskDao.updateCompleted(taskId, true)
    }

    override suspend fun activateTask(service: ApiService, task: Task) = withContext(ioDispatcher) {
        taskDao.updateCompleted(task.id, false)
    }

    override suspend fun activateTask(service: ApiService, taskId: Int) {
        taskDao.updateCompleted(taskId, false)
    }

    override suspend fun clearCompletedTasks(service: ApiService) {
        taskDao.deleteCompletedTasks()
    }

    override suspend fun deleteAllTasks(service: ApiService) = withContext(ioDispatcher) {
        taskDao.deleteTasks()
    }

    override suspend fun deleteTask(service: ApiService, taskId: Int)  = withContext<Unit>(ioDispatcher) {
        taskDao.deleteTaskById(taskId)
    }

}