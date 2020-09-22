package software.yesaya.sajo.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import software.yesaya.sajo.MainCoroutineRule
import software.yesaya.sajo.R
import software.yesaya.sajo.data.source.FakeTestRepository
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.getOrAwaitValue
import software.yesaya.sajo.tasks.ui.list.TasksFilterType
import software.yesaya.sajo.tasks.ui.list.TasksViewModel
import software.yesaya.sajo.utils.Event

/**
 * Unit tests for the implementation of [TasksViewModel]
 */
@ExperimentalCoroutinesApi
class TasksViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tasksRepository: FakeTestRepository

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        tasksRepository = FakeTestRepository()
        val task1 = Task(1, "Title1", "Description1", false, "2020-01-01", null)
        val task2 = Task(2, "Title2", "Description2", true, "2020-01-01", null)
        val task3 = Task(3, "Title3", "Description3", true, "2020-01-01", null)
        tasksRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(tasksRepository)
    }

    @Test
    fun addNewTask_setsNewTaskEvent() {
        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()

        MatcherAssert.assertThat(
            value.getContentIfNotHandled(),
            CoreMatchers.not(CoreMatchers.nullValue())
        )

    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {
        // When the filter type is ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        MatcherAssert.assertThat(
            tasksViewModel.tasksAddViewVisible.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        // Create an active task and add it to the repository.
        val task = Task(4, "Title1", "Description1", false, "2020-01-01", null)
        tasksRepository.addTasks(task)

        // Mark the task as complete task.
        tasksViewModel.completeTask(task, true)

        // Verify the task is completed.
        MatcherAssert.assertThat(
            tasksRepository.tasksServiceData[task.id]?.completed,
            `is`(true)
        )

        // Assert that the snackbar has been updated with the correct text.
        val snackbarText: Event<Int> =  tasksViewModel.snackbarText.getOrAwaitValue()
        MatcherAssert.assertThat(
            snackbarText.getContentIfNotHandled(),
            `is`(R.string.task_marked_complete)
        )
    }

}