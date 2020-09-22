package software.yesaya.sajo

import timber.log.Timber
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.facebook.stetho.Stetho
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import software.yesaya.sajo.data.repositories.tasks.TasksRepository
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.network.Network
import software.yesaya.sajo.data.sources.remote.network.TokenManager
import software.yesaya.sajo.work.RefreshDataWork
import java.util.concurrent.TimeUnit

class SajoApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    val taskRepository: TasksRepository
        get() = ServiceLocator.provideTasksRepository(this)

    var service: ApiService? = null
    var tokenManager: TokenManager? = null

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )

        service = Network.createService(ApiService::class.java)

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", Context.MODE_PRIVATE))

        Timber.plant(Timber.DebugTree())

        delayInit()

        initStetho()
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this)
    }

    private fun delayInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true).build()

        val repeatingRequests = PeriodicWorkRequestBuilder<RefreshDataWork> (
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequests
        )
    }

}