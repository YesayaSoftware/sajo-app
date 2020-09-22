package software.yesaya.sajo.tasks.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import software.yesaya.sajo.data.repositories.tasks.TasksRepository
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.network.TokenManager

@Suppress("UNCHECKED_CAST")
class StatisticsViewModelFactory (
    private var service: ApiService?,
    private val tokenManager: TokenManager?,
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (StatisticsViewModel(service, tokenManager, tasksRepository) as T)
}