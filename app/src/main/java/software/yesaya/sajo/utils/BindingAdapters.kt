package software.yesaya.sajo.utils

import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.tasks.adapter.TaskAdapter


/**
 * [BindingAdapter]s for the [Task]s list.
 */
@BindingAdapter("items")
fun setItems(listView: RecyclerView, items: List<Task>?) {
    items?.let {
        (listView.adapter as TaskAdapter).submitList(items)
    }
}

@BindingAdapter("completedTask")
fun setStyle(textView: TextView, enabled: Boolean) {
    if (enabled) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "tasks")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, tasks: Any?) {
    view.visibility = if (tasks != null) View.GONE else View.VISIBLE

    if (isNetWorkError) {
        view.visibility = View.GONE
    }
}
