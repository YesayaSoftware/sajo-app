package software.yesaya.sajo.statistics

import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test
import software.yesaya.sajo.data.sources.local.entities.Task
import software.yesaya.sajo.tasks.ui.statistics.getActiveAndCompletedStats

/**
 * Unit tests for [getActiveAndCompletedStats].
 */
class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        val tasks = listOf(
            Task(1, "Title1", "Description1", completed = false, created_at = "", updated_at = null)
        )
        // When the list of tasks is computed with an active task
        val result = getActiveAndCompletedStats(tasks)

        // Then the percentages are 100 and 0
        Assert.assertThat(result.activeTasksPercent, Is.`is`(100f))
        Assert.assertThat(result.completedTasksPercent, Is.`is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_noActive_returnsZeroHundred() {
        val tasks = listOf(
            Task(1, "Title1", "Description1", completed = true, created_at = "", updated_at = null)
        )
        // When the list of tasks is computed with a completed task
        val result = getActiveAndCompletedStats(tasks)

        // Then the percentages are 0 and 100
        Assert.assertThat(result.activeTasksPercent, Is.`is`(0f))
        Assert.assertThat(result.completedTasksPercent, Is.`is`(100f))
    }

    @Test
    fun getActiveAndCompletedStats_both_returnsFortySixty() {
        // Given 3 completed tasks and 2 active tasks
        val tasks = listOf(
            Task(1, "Title1", "Description1", true, "2020-01-01", null),
            Task(2, "Title2", "Description2", true, "2020-01-01", null),
            Task(3, "Title3", "Description3", true, "2020-01-01", null),
            Task(4, "Title3", "Description3", false, "2020-01-01", null),
            Task(5, "Title3", "Description3", false, "2020-01-01", null)
        )

        // When the list of tasks is computed
        val result = getActiveAndCompletedStats(tasks)

        // Then the result is 40-60
        Assert.assertThat(result.activeTasksPercent, Is.`is`(40f))
        Assert.assertThat(result.completedTasksPercent, Is.`is`(60f))
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeros() {
        // When there's an error loading stats
        val result = getActiveAndCompletedStats(null)

        // Both active and completed tasks are 0
        Assert.assertThat(result.activeTasksPercent, Is.`is`(0f))
        Assert.assertThat(result.completedTasksPercent, Is.`is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_empty_returnsZeros() {
        // When there are no tasks
        val result = getActiveAndCompletedStats(emptyList())

        // Both active and completed tasks are 0
        Assert.assertThat(result.activeTasksPercent, Is.`is`(0f))
        Assert.assertThat(result.completedTasksPercent, Is.`is`(0f))
    }
}