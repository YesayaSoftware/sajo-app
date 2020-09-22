package software.yesaya.sajo.tasks.ui.statistics

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import software.yesaya.sajo.data.repositories.tasks.TasksRepository
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.data.sources.Result
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.network.TokenManager

/**
 * ViewModel for the statistics screen.
 */
class StatisticsViewModel(
    private var service: ApiService?,
    private val tokenManager: TokenManager?,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val tasks: LiveData<Result<List<Task>>> = tasksRepository.observeTasks()

    private val _dataLoading = MutableLiveData<Boolean>(false)

    private val stats: LiveData<StatsResult?> = tasks.map {
        if (it is Result.Success) {
            getActiveAndCompletedStats(it.data)
        } else {
            null
        }
    }

    val activeTasksPercent = stats.map { it?.activeTasksPercent ?: 0f }
    val completedTasksPercent: LiveData<Float> = stats.map { it?.completedTasksPercent ?: 0f }
    val dataLoading: LiveData<Boolean> = _dataLoading

    val error: LiveData<Boolean> = tasks.map { it is Result.Error }

    val empty: LiveData<Boolean> = tasks.map { (it as? Result.Success)?.data.isNullOrEmpty() }

    fun refresh() {
        _dataLoading.value = true
            viewModelScope.launch {
                tasksRepository.refreshTasks()
                _dataLoading.value = false
            }
    }
}
