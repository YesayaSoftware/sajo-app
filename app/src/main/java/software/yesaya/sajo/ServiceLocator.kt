package software.yesaya.sajo

import android.content.Context
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.runBlocking
import software.yesaya.sajo.data.repositories.tasks.TasksRepository
import software.yesaya.sajo.data.repositories.tasks.TasksRepositoryImp
import software.yesaya.sajo.data.sources.TasksDataSource
import software.yesaya.sajo.data.sources.local.TasksLocalDataSource
import software.yesaya.sajo.data.sources.local.database.SajoDatabase
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.TasksRemoteDataSource
import software.yesaya.sajo.data.sources.remote.network.Network
import software.yesaya.sajo.data.sources.remote.network.TokenManager
import timber.log.Timber

/**
 * A Service Locator for the [TasksRepository]. This is the prod version, with a
 * the "real" [TasksRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: SajoDatabase? = null
    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set


    var apiService: ApiService? = null

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo = TasksRepositoryImp((context.applicationContext as SajoApplication).tokenManager!!, TasksRemoteDataSource, createTaskLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: SajoDatabase.getDatabase(context)

        return TasksLocalDataSource(database.taskDao)
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks(apiService!!)
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
}