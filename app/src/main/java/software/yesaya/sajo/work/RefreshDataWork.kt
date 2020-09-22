package software.yesaya.sajo.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.HttpException
import software.yesaya.sajo.SajoApplication
import software.yesaya.sajo.ServiceLocator.provideTasksRepository
import software.yesaya.sajo.data.repositories.tasks.TasksRepository
import timber.log.Timber

class RefreshDataWork(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    var tasksRepository: TasksRepository? = null

    companion object {
        const val WORK_NAME = "RefreshDataWorker"

    }

    override suspend fun doWork(): Result {
        tasksRepository = provideTasksRepository(applicationContext)

        return try {
            if ((this.applicationContext as SajoApplication).tokenManager!!.getTokens().access_token != null) {
                tasksRepository!!.refreshTasks()

                Timber.d("Work request for sync is run")

                Result.success()
            } else {
                Result.failure()
            }
        } catch (exception: HttpException) {
            Result.retry()
        }
    }
}