package software.yesaya.sajo.tasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.databinding.TaskItemBinding
import software.yesaya.sajo.tasks.ui.list.TasksViewModel

class TaskViewHolder private constructor(val binding: TaskItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(viewModel: TasksViewModel, item: Task) {
        binding.viewModel = viewModel
        binding.task = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): TaskViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TaskItemBinding.inflate(layoutInflater, parent, false)

            return TaskViewHolder(binding)
        }
    }
}