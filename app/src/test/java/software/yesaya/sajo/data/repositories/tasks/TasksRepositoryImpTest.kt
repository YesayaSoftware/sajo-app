package software.yesaya.sajo.data.repositories.tasks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import software.yesaya.sajo.MainCoroutineRule
import software.yesaya.sajo.data.source.FakeDataSource
import software.yesaya.sajo.data.sources.Result
import software.yesaya.sajo.data.sources.local.entities.Task

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
@ExperimentalCoroutinesApi
class TasksRepositoryImpTest {

    private val task1 = Task(1, "Title1", "Description1", false, "2020-01-01", null, null)
    private val task2 = Task(2, "Title2", "Description2", false, "2020-01-01", null, null)
    private val task3 = Task(3, "Title3", "Description3", false, "2020-01-01", null, null)
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }
    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var tasksRepository: TasksRepositoryImp

    // Set the main coroutines dispatcher for unit testing.
    @get:
    Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createRepository() {
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())

        // Get a reference to the class under test
        tasksRepository = TasksRepositoryImp(
            tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Main
        )
    }

    @Test
    fun getTasks_requestsAllTasksFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(true) as Result.Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(remoteTasks))
    }
}
