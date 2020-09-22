package software.yesaya.sajo.tasks.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import software.yesaya.sajo.data.repositories.tasks.TasksRepository


@Suppress("UNCHECKED_CAST")
class TaskDetailViewModelFactory(
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (TaskDetailViewModel(tasksRepository) as T)
}