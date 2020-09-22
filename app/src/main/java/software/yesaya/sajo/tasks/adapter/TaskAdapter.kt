package software.yesaya.sajo.tasks.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.tasks.ui.list.TasksViewModel

class TaskAdapter(private val viewModel: TasksViewModel) : ListAdapter<Task, TaskViewHolder>(TaskDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }
}