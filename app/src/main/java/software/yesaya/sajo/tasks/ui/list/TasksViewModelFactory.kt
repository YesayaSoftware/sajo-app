package software.yesaya.sajo.tasks.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import software.yesaya.sajo.data.repositories.tasks.TasksRepository
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.network.TokenManager

@Suppress("UNCHECKED_CAST")
class TasksViewModelFactory (
    private var service: ApiService?,
    private val tokenManager: TokenManager?,
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (TasksViewModel(service, tokenManager, tasksRepository) as T)
}