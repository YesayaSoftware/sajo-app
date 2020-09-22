package software.yesaya.sajo.tasks.ui.list

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import software.yesaya.sajo.MainActivity
import software.yesaya.sajo.R
import software.yesaya.sajo.SajoApplication
import software.yesaya.sajo.auth.login.LoginActivity
import software.yesaya.sajo.databinding.FragmentTasksBinding
import software.yesaya.sajo.tasks.adapter.TaskAdapter
import software.yesaya.sajo.utils.EventObserver
import software.yesaya.sajo.utils.setupRefreshLayout
import software.yesaya.sajo.utils.setupSnackbar
import timber.log.Timber

class TasksFragment : Fragment() {
    private val viewModel by viewModels<TasksViewModel> {
        TasksViewModelFactory(
            (requireContext().applicationContext as SajoApplication).service,
            (requireContext().applicationContext as SajoApplication).tokenManager,
            (requireContext().applicationContext as SajoApplication).taskRepository
        )
    }

    private lateinit var binding: FragmentTasksBinding

    private lateinit var listAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTasksBinding.inflate(inflater, container, false).apply {
            viewModel = viewModel
        }

        setHasOptionsMenu(true)

        binding.viewModel = viewModel

        // Observer for the network error.
        viewModel.eventNetworkError.observe(
            viewLifecycleOwner,
            Observer { isNetworkError ->
                if (isNetworkError) onNetworkError()
            })

        viewModel.hasToken.observe(viewLifecycleOwner, Observer { hasToken ->
            if (hasToken == false) {
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        binding.lifecycleOwner = this.viewLifecycleOwner

        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout(binding.refreshLayout, binding.tasksList)
        setupNavigation()
        setupFab()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_task_fab)?.let {
            it.setOnClickListener {
                navigateToAddNewTask()
            }
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections
            .actionTasksFragmentToAddEditTaskFragment(
                0,
                resources.getString(R.string.add_task)
            )
        findNavController().navigate(action)
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
        })

        viewModel.newTaskEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddNewTask()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_clear -> {
                viewModel.clearCompletedTasks()
                true
            }

            R.id.action_theme -> {
                // Get new mode.
                val mode =
                    if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                        Configuration.UI_MODE_NIGHT_NO
                    ) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    }

                // Change UI Mode
                AppCompatDelegate.setDefaultNightMode(mode)
                true
            }

            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }

            R.id.menu_reset -> {
                viewModel.deleteAllTasks()
                true
            }

            R.id.menu_refresh -> {
                viewModel.loadTasks(true)
                true
            }

            R.id.menu_logout -> {
                viewModel.logout()
                true
            }
            else -> false
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    private fun setupListAdapter() {
        val viewModel = binding.viewModel
        if (viewModel != null) {
            listAdapter = TaskAdapter(viewModel)
            binding.tasksList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun openTaskDetails(taskId: Int) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(taskId)
        findNavController().navigate(action)
    }
}